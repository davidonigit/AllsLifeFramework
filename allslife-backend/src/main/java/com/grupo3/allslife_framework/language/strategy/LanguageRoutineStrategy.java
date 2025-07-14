package com.grupo3.allslife_framework.language.strategy;

import java.util.Locale.LanguageRange;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.grupo3.allslife_framework.framework.dto.NotificationDTO;
import com.grupo3.allslife_framework.framework.model.RoutineHistory;
import com.grupo3.allslife_framework.framework.repository.RoutineHistoryRepository;
import com.grupo3.allslife_framework.framework.service.NotificationService;
import com.grupo3.allslife_framework.framework.strategy.RoutineGenerationStrategy;
import com.grupo3.allslife_framework.language.model.LanguageRoutine;
import com.grupo3.allslife_framework.language.model.LanguageUserPreferences;

@Service
public class LanguageRoutineStrategy implements RoutineGenerationStrategy<LanguageRoutine> {

    @Autowired
    private RoutineHistoryRepository historyRepository;

    @Autowired
    private NotificationService notificationService;

    @Override
    public void validateRoutineForGeneration(LanguageRoutine routine) {
        if (routine.getLanguageName() == null || routine.getLanguageName().isEmpty()) {
            throw new IllegalArgumentException("Idioma não definido para a rotina.");
        }
        if (routine.getWeeklyAvailability().isEmpty()) {
            throw new IllegalArgumentException("Rotina não possui disponibilidade definida.");
        }
    }

    @Override
    public String buildGenerationPrompt(LanguageRoutine routine, String... feedback) {
        String prompt = "Responda como especialista em aprendizado de idiomas. Idioma: " + routine.getLanguageName() + ". ";

        if (feedback != null && feedback.length > 0 && !feedback[0].isEmpty()) {
            prompt += "Considere o feedback: " + feedback[0] + ". ";
        }

        var user = routine.getUser();
        if (user.getPreferences() instanceof LanguageUserPreferences prefs) {
            prompt += "Habilidade em foco: " + prefs.getLanguageSkill() + ", Experiência: " + prefs.getExperienceLevel() + ". ";
        }

        prompt += "Monte a rotina de estudo de idioma em Markdown para os dias disponíveis.";
        return prompt;
    }

    @Override
    public void saveHistory(LanguageRoutine routine) {
        if (routine.getGeneratedRoutine() != null) {
            RoutineHistory history = new RoutineHistory();
            history.setGeneratedRoutine(routine.getGeneratedRoutine());
            history.setRoutineName(routine.getLanguageName());
            history.setUser(routine.getUser());
            historyRepository.save(history);
        }
    }

    @Override
    public void sendSuccessNotification(LanguageRoutine routine) {
        NotificationDTO dto = new NotificationDTO(
            "Rotina de idioma gerada!",
            "Sua rotina para " + routine.getLanguageName() + " foi gerada!",
            routine.getUser().getId()
        );
        notificationService.create(dto);
    }
}
