package com.grupo3.allslife_framework.sports.strategy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.grupo3.allslife_framework.framework.dto.NotificationDTO;
import com.grupo3.allslife_framework.framework.model.RoutineHistory;
import com.grupo3.allslife_framework.framework.repository.RoutineHistoryRepository;
import com.grupo3.allslife_framework.framework.service.NotificationService;
import com.grupo3.allslife_framework.framework.strategy.RoutineGenerationStrategy;
import com.grupo3.allslife_framework.sports.model.SportRoutine;
import com.grupo3.allslife_framework.sports.model.SportUserPreferences;

@Service
public class SportRoutineStrategy implements RoutineGenerationStrategy<SportRoutine> {

    @Autowired
    private RoutineHistoryRepository historyRepository;

    @Autowired
    private NotificationService notificationService;

    @Override
    public void validateRoutineForGeneration(SportRoutine routine) {
        if (routine.getSportName() == null || routine.getSportName().isEmpty()) {
            throw new IllegalArgumentException("Esporte não definido para a rotina.");
        }
        if (routine.getWeeklyAvailability().isEmpty()) {
            throw new IllegalArgumentException("Rotina não possui disponibilidade definida.");
        }
    }

    @Override
    public String buildGenerationPrompt(SportRoutine routine, String... feedback) {
        String prompt = "Responda como especialista em esportes. Esporte: " + routine.getSportName() + ". ";

        if (feedback != null && feedback.length > 0 && !feedback[0].isEmpty()) {
            prompt += "Considere o feedback: " + feedback[0] + ". ";
        }

        var user = routine.getUser();
        if (user.getPreferences() instanceof SportUserPreferences prefs) {
            prompt += "Idade: " + prefs.getAge() + ", Experiência: " + prefs.getExperienceLevel() + ". ";
        }

        prompt += "Monte a rotina de treino em Markdown para os dias disponíveis.";
        return prompt;
    }

    @Override
    public void saveHistory(SportRoutine routine) {
        if (routine.getGeneratedRoutine() != null) {
            RoutineHistory history = new RoutineHistory();
            history.setGeneratedRoutine(routine.getGeneratedRoutine());
            history.setSportName(routine.getSportName());
            history.setUser(routine.getUser());
            historyRepository.save(history);
        }
    }

    @Override
    public void sendSuccessNotification(SportRoutine routine) {
        NotificationDTO dto = new NotificationDTO(
            "Rotina de esporte gerada!",
            "Sua rotina para " + routine.getSportName() + " foi gerada!",
            routine.getUser().getId()
        );
        notificationService.create(dto);
    }
}
