package com.grupo3.allslife_framework.sports.service;

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
import com.grupo3.allslife_framework.sports.dto.SportRoutineDTO;
import com.grupo3.allslife_framework.sports.dto.SportUserPreferencesDTO;
import com.grupo3.allslife_framework.sports.model.SportRoutine;
import com.grupo3.allslife_framework.sports.model.SportUserPreferences;
import com.grupo3.allslife_framework.sports.repository.SportRoutineRepository;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class SportRoutineService extends AbstractRoutineService<SportRoutine, SportRoutineRepository> {

    private final RoutineHistoryRepository routineHistoryRepository;

    public SportRoutineService(
        SportRoutineRepository routineRepository,
        DailyAvailabilityRepository dailyAvailabilityRepository,
        SecurityUtils securityUtils,
        FachadaLLM fachadaLLM,
        NotificationService notificationService,
        UserService userService,
        RoutineHistoryRepository routineHistoryRepository,
        RoutineGenerationStrategy<SportRoutine> generationStrategy // <- injeta a strategy
    ) {
        super(routineRepository, dailyAvailabilityRepository, securityUtils, fachadaLLM, notificationService, userService, generationStrategy);
        this.routineHistoryRepository = routineHistoryRepository;
    }

    // --- MÉTODOS ESPECÍFICOS DE SportRoutineService ---

    public SportRoutine getOrCreateSportRoutineForCurrentUser() {
        User currentUser = userService.getById(securityUtils.getCurrentUserId());

        if (currentUser.getRoutine() instanceof SportRoutine existingRoutine) {
            return existingRoutine;
        }

        SportRoutine newRoutine = new SportRoutine();
        newRoutine.setUser(currentUser);
        currentUser.setRoutine(newRoutine);

        routineRepository.save(newRoutine);
        initializeWeeklyAvailability(newRoutine.getId());

        return newRoutine;
    }

    public SportUserPreferences getOrCreateSportPreferences() {
        User currentUser = userService.getById(securityUtils.getCurrentUserId());

        if (currentUser.getPreferences() instanceof SportUserPreferences existingPrefs) {
            return existingPrefs;
        }

        SportUserPreferences prefs = new SportUserPreferences();
        prefs.setUser(currentUser);
        currentUser.setPreferences(prefs);

        userService.saveUser(currentUser);
        return prefs;
    }

    public SportRoutine updateSportRoutine(SportRoutineDTO dto) {
        SportRoutine sportRoutine = findByCurrentUser();
        if (sportRoutine == null) {
            throw new RoutineNotFoundException("Rotina esportiva não encontrada para o usuário atual.");
        }

        sportRoutine.setSportName(dto.sport());

        // Atualizar disponibilidade semanal
        dto.weeklyAvailability().forEach(dailyAvailability ->
            sportRoutine.updateAvailability(
                dailyAvailability.dayOfWeek(),
                dailyAvailability.morningAvailable(),
                dailyAvailability.afternoonAvailable(),
                dailyAvailability.eveningAvailable()
            )
        );

        return routineRepository.save(sportRoutine);
    }

    public SportUserPreferences updateSportUserPreferences(SportUserPreferencesDTO dto) {
        User currentUser = userService.getById(securityUtils.getCurrentUserId());

        if (!(currentUser.getPreferences() instanceof SportUserPreferences preferences)) {
            throw new IllegalArgumentException("Usuário não possui preferências esportivas definidas.");
        }

        preferences.setAge(dto.age());
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
