package com.grupo3.allslife_framework.studies.service;

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
import com.grupo3.allslife_framework.studies.dto.StudyRoutineDTO;
import com.grupo3.allslife_framework.studies.dto.StudyUserPreferencesDTO;
import com.grupo3.allslife_framework.studies.model.StudyRoutine;
import com.grupo3.allslife_framework.studies.model.StudyUserPreferences;
import com.grupo3.allslife_framework.studies.repository.StudyRoutineRepository;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class StudyRoutineService extends AbstractRoutineService<StudyRoutine, StudyRoutineRepository> {

    private final RoutineHistoryRepository routineHistoryRepository;

    public StudyRoutineService(
        StudyRoutineRepository routineRepository,
        DailyAvailabilityRepository dailyAvailabilityRepository,
        SecurityUtils securityUtils,
        FachadaLLM fachadaLLM,
        NotificationService notificationService,
        UserService userService,
        RoutineHistoryRepository routineHistoryRepository,
        RoutineGenerationStrategy<StudyRoutine> generationStrategy // <- injeta a strategy
    ) {
        super(routineRepository, dailyAvailabilityRepository, securityUtils, fachadaLLM, notificationService, userService, generationStrategy);
        this.routineHistoryRepository = routineHistoryRepository;
    }

    // --- MÉTODOS ESPECÍFICOS DE StudyRoutineService ---

    public StudyRoutine getOrCreateStudyRoutineForCurrentUser() {
        User currentUser = userService.getById(securityUtils.getCurrentUserId());

        if (currentUser.getRoutine() instanceof StudyRoutine existingRoutine) {
            return existingRoutine;
        }

        StudyRoutine newRoutine = new StudyRoutine();
        newRoutine.setUser(currentUser);
        currentUser.setRoutine(newRoutine);

        routineRepository.save(newRoutine);
        initializeWeeklyAvailability(newRoutine.getId());

        return newRoutine;
    }

    public StudyUserPreferences getOrCreateStudyPreferences() {
        User currentUser = userService.getById(securityUtils.getCurrentUserId());

        if (currentUser.getPreferences() instanceof StudyUserPreferences existingPrefs) {
            return existingPrefs;
        }

        StudyUserPreferences prefs = new StudyUserPreferences();
        prefs.setUser(currentUser);
        currentUser.setPreferences(prefs);

        userService.saveUser(currentUser);
        return prefs;
    }

    public StudyRoutine updateStudyRoutine(StudyRoutineDTO dto) {
        StudyRoutine studyRoutine = findByCurrentUser();
        if (studyRoutine == null) {
            throw new RoutineNotFoundException("Rotina de estudos não encontrada para o usuário atual.");
        }

        studyRoutine.setSubjectArea(dto.subjectArea());

        // Atualizar disponibilidade semanal
        dto.weeklyAvailability().forEach(dailyAvailability ->
            studyRoutine.updateAvailability(
                dailyAvailability.dayOfWeek(),
                dailyAvailability.morningAvailable(),
                dailyAvailability.afternoonAvailable(),
                dailyAvailability.eveningAvailable()
            )
        );

        return routineRepository.save(studyRoutine);
    }

    public StudyUserPreferences updateStudyUserPreferences(StudyUserPreferencesDTO dto) {
        User currentUser = userService.getById(securityUtils.getCurrentUserId());

        if (!(currentUser.getPreferences() instanceof StudyUserPreferences preferences)) {
            throw new IllegalArgumentException("Usuário não possui preferências de estudos definidas.");
        }

        preferences.setMaterialPreferred(dto.materialPreferred());
        preferences.setMaxTime(dto.maxTime());

        userService.saveUser(currentUser);

        return preferences;
    }

    @Override
    public List<RoutineHistory> getRoutineHistory() {
        Long userId = securityUtils.getCurrentUserId();
        return routineHistoryRepository.findByUserId(userId);
    }
}

