package com.grupo3.allslife_framework.studies.strategy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.grupo3.allslife_framework.framework.dto.NotificationDTO;
import com.grupo3.allslife_framework.framework.model.RoutineHistory;
import com.grupo3.allslife_framework.framework.repository.RoutineHistoryRepository;
import com.grupo3.allslife_framework.framework.service.NotificationService;
import com.grupo3.allslife_framework.framework.strategy.RoutineGenerationStrategy;
import com.grupo3.allslife_framework.studies.model.StudyRoutine;
import com.grupo3.allslife_framework.studies.model.StudyUserPreferences;

@Service
public class StudyRoutineStrategy implements RoutineGenerationStrategy<StudyRoutine> {

    @Autowired
    private RoutineHistoryRepository historyRepository;

    @Autowired
    private NotificationService notificationService;

    @Override
    public void validateRoutineForGeneration(StudyRoutine routine) {
        if (routine.getSubjectArea() == null || routine.getSubjectArea().isEmpty()) {
            throw new IllegalArgumentException("Área de estudo não definida para a rotina.");
        }
        if (routine.getWeeklyAvailability().isEmpty()) {
            throw new IllegalArgumentException("Rotina não possui disponibilidade definida.");
        }
    }

    @Override
    public String buildGenerationPrompt(StudyRoutine routine, String availabilityString, String... feedback) {
        String prompt = "Responda como especialista em educação. Área de estudo: " + routine.getSubjectArea() + ". ";

        if (feedback != null && feedback.length > 0 && !feedback[0].isEmpty()) {
            prompt += "Considere o feedback: " + feedback[0] + ". ";
        }

        var user = routine.getUser();
        if (user.getPreferences() instanceof StudyUserPreferences prefs) {
            prompt += "Material preferido: " + prefs.getMaterialPreferred() + ", Tempo máximo por sessão: " + prefs.getMaxTime() + " minutos. ";
        }

        prompt += "Considere os dias disponíveis: " + availabilityString + "Monte a rotina de estudos em Markdown para os dias disponíveis. Crie uma divisória para cada dia da semana, não crie divisórias de horários dentro dos turnos, com os dias da semana em português, a fim de facilitar a leitura. Use uma linguagem clara e objetiva, evitando jargões técnicos. A rotina deve ser adaptada ao material preferido e tempo máximo do usuário. Evite descrições longas no inicio, crie apenas uma seção de informações importantes no inicio, em seguida, já indique os dias da semana com as atividades. ";
        return prompt;
    }

    @Override
    public void saveHistory(StudyRoutine routine) {
        if (routine.getGeneratedRoutine() != null) {
            RoutineHistory history = new RoutineHistory();
            history.setGeneratedRoutine(routine.getGeneratedRoutine());
            history.setRoutineName(routine.getSubjectArea());
            history.setUser(routine.getUser());
            historyRepository.save(history);
        }
    }

    @Override
    public void sendSuccessNotification(StudyRoutine routine) {
        NotificationDTO dto = new NotificationDTO(
            "Rotina de estudos gerada!",
            "Sua rotina para " + routine.getSubjectArea() + " foi gerada!",
            routine.getUser().getId()
        );
        notificationService.create(dto);
    }
}

