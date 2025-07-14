package com.grupo3.allslife_framework.language.service;

import com.grupo3.allslife_framework.framework.exception.RoutineNotFoundException;
import com.grupo3.allslife_framework.framework.model.RoutineHistory;
import com.grupo3.allslife_framework.framework.model.User;
import com.grupo3.allslife_framework.framework.repository.DailyAvailabilityRepository;
import com.grupo3.allslife_framework.framework.repository.RoutineHistoryRepository;
import com.grupo3.allslife_framework.framework.security.SecurityUtils;
import com.grupo3.allslife_framework.framework.service.AbstractRoutineService;
import com.grupo3.allslife_framework.framework.service.FachadaLLM;
import com.grupo3.allslife_framework.framework.service.NotificationService;
import com.grupo3.allslife_framework.framework.service.UserService;
import com.grupo3.allslife_framework.framework.strategy.RoutineGenerationStrategy;
import com.grupo3.allslife_framework.language.dto.LanguageRoutineDTO;
import com.grupo3.allslife_framework.language.dto.LanguageUserPreferencesDTO;
import com.grupo3.allslife_framework.language.model.LanguageRoutine;
import com.grupo3.allslife_framework.language.model.LanguageUserPreferences;
import com.grupo3.allslife_framework.language.repository.LanguageRoutineRepository;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class LanguageRoutineService extends AbstractRoutineService<LanguageRoutine, LanguageRoutineRepository> {

    private final RoutineHistoryRepository routineHistoryRepository;

    public LanguageRoutineService(
        LanguageRoutineRepository routineRepository,
        DailyAvailabilityRepository dailyAvailabilityRepository,
        SecurityUtils securityUtils,
        FachadaLLM fachadaLLM,
        NotificationService notificationService,
        UserService userService,
        RoutineHistoryRepository routineHistoryRepository,
        RoutineGenerationStrategy<LanguageRoutine> generationStrategy
    ) {
        super(routineRepository, dailyAvailabilityRepository, securityUtils, fachadaLLM, notificationService, userService, generationStrategy);
        this.routineHistoryRepository = routineHistoryRepository;
    }

    // --- MÉTODOS ESPECÍFICOS DE LanguageRoutineService ---

    public LanguageRoutine getOrCreateLanguageRoutineForCurrentUser() {
        User currentUser = userService.getById(securityUtils.getCurrentUserId());

        if (currentUser.getRoutine() instanceof LanguageRoutine existingRoutine) {
            return existingRoutine;
        }

        LanguageRoutine newRoutine = new LanguageRoutine();
        newRoutine.setUser(currentUser);
        currentUser.setRoutine(newRoutine);

        routineRepository.save(newRoutine);
        initializeWeeklyAvailability(newRoutine.getId());

        return newRoutine;
    }

    public LanguageUserPreferences getOrCreateLanguagePreferences() {
        User currentUser = userService.getById(securityUtils.getCurrentUserId());

        if (currentUser.getPreferences() instanceof LanguageUserPreferences existingPrefs) {
            return existingPrefs;
        }

        LanguageUserPreferences prefs = new LanguageUserPreferences();
        prefs.setUser(currentUser);
        currentUser.setPreferences(prefs);

        userService.saveUser(currentUser);
        return prefs;
    }

    public LanguageRoutine updateLanguageRoutine(LanguageRoutineDTO dto) {
        LanguageRoutine LanguageRoutine = findByCurrentUser();
        if (LanguageRoutine == null) {
            throw new RoutineNotFoundException("Rotina de Idioma não encontrada para o usuário atual.");
        }

        LanguageRoutine.setLanguageName(dto.languageName());

        // Atualizar disponibilidade semanal
        dto.weeklyAvailability().forEach(dailyAvailability ->
            LanguageRoutine.updateAvailability(
                dailyAvailability.dayOfWeek(),
                dailyAvailability.morningAvailable(),
                dailyAvailability.afternoonAvailable(),
                dailyAvailability.eveningAvailable()
            )
        );

        return routineRepository.save(LanguageRoutine);
    }

    public LanguageUserPreferences updateLanguageUserPreferences(LanguageUserPreferencesDTO dto) {
        User currentUser = userService.getById(securityUtils.getCurrentUserId());

        if (!(currentUser.getPreferences() instanceof LanguageUserPreferences preferences)) {
            throw new IllegalArgumentException("Usuário não possui preferências de idioma definidas.");
        }

        preferences.setLanguageSkill(dto.languageSkill());
        preferences.setExperienceLevel(dto.experienceLevel());

        userService.saveUser(currentUser);

        return preferences;
    }

    @Override
    public List<RoutineHistory> getRoutineHistory() {
        Long userId = securityUtils.getCurrentUserId();
        return routineHistoryRepository.findByUserId(userId);
    }
}
