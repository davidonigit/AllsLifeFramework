package com.grupo3.allslife_framework.framework.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties.User;

import com.grupo3.allslife_framework.framework.enums.DayOfWeekEnum;
import com.grupo3.allslife_framework.framework.exception.RoutineNotFoundException;
import com.grupo3.allslife_framework.framework.exception.UserNotFoundException;
import com.grupo3.allslife_framework.framework.model.AbstractRoutine;
import com.grupo3.allslife_framework.framework.model.DailyAvailability;
import com.grupo3.allslife_framework.framework.model.RoutineHistory;
import com.grupo3.allslife_framework.framework.repository.AbstractRoutineRepository;
import com.grupo3.allslife_framework.framework.repository.DailyAvailabilityRepository;
import com.grupo3.allslife_framework.framework.security.SecurityUtils;
import com.grupo3.allslife_framework.framework.strategy.RoutineGenerationStrategy;

/**
 * Serviço base abstrato que define o fluxo de operações para qualquer tipo de rotina.
 * Agora usando o padrão Strategy para customizar partes variáveis.
 *
 * @param <T> O tipo da entidade de rotina.
 * @param <R> O tipo do repositório para a rotina.
 */
public abstract class AbstractRoutineService<
    T extends AbstractRoutine,
    R extends AbstractRoutineRepository<T>
> {

    @Autowired
    protected R routineRepository;

    @Autowired
    protected DailyAvailabilityRepository dailyAvailabilityRepository;

    @Autowired
    protected SecurityUtils securityUtils;

    @Autowired
    protected FachadaLLM fachadaLLM;

    @Autowired
    protected NotificationService notificationService;

    @Autowired
    protected UserService userService;

    // Injeção da Strategy específica
    protected final RoutineGenerationStrategy<T> generationStrategy;

    // Construtor
    public AbstractRoutineService(
        R routineRepository,
        DailyAvailabilityRepository dailyAvailabilityRepository,
        SecurityUtils securityUtils,
        FachadaLLM fachadaLLM,
        NotificationService notificationService,
        UserService userService,
        RoutineGenerationStrategy<T> generationStrategy
    ) {
        this.routineRepository = routineRepository;
        this.dailyAvailabilityRepository = dailyAvailabilityRepository;
        this.securityUtils = securityUtils;
        this.fachadaLLM = fachadaLLM;
        this.notificationService = notificationService;
        this.userService = userService;
        this.generationStrategy = generationStrategy;
    }

    // MÉTODOS GENÉRICOS

    public Optional<T> findById(Long id) {
        return routineRepository.findById(id);
    }

    public T findByCurrentUser() {
        Long userId = securityUtils.getCurrentUserId();
        if (userId == null) {
            throw new UserNotFoundException("Nenhum usuário logado.");
        }
        return routineRepository.findByUserId(userId)
                .orElseThrow(() -> new RoutineNotFoundException("Rotina não encontrada para o usuário com ID: " + userId));
    }

    public T initializeWeeklyAvailability(Long routineId) {
        T routine = routineRepository.findById(routineId)
                .orElseThrow(() -> new RoutineNotFoundException("Rotina não encontrada com ID: " + routineId));

        routine.getWeeklyAvailability().clear();
        for (DayOfWeekEnum day : DayOfWeekEnum.values()) {
            DailyAvailability availability = new DailyAvailability(day, false, false, false, routine);
            routine.getWeeklyAvailability().add(availability);
        }
        return routineRepository.save(routine);
    }

    public T updateDailyAvailability(Long routineId, DayOfWeekEnum day, boolean morning, boolean afternoon, boolean evening) {
        T routine = findById(routineId)
                .orElseThrow(() -> new RoutineNotFoundException("Rotina não encontrada com ID: " + routineId));
        routine.updateAvailability(day, morning, afternoon, evening);
        return routineRepository.save(routine);
    }

    protected String getWeekAvailabilityString(T routine) {
        StringBuilder availability = new StringBuilder();
        for (DailyAvailability dailyAvailability : routine.getWeeklyAvailability()) {
            availability.append(dailyAvailability.getDayOfWeek())
                .append(": ")
                .append("Manhã: ").append(dailyAvailability.isMorningAvailable() ? "Disponível" : "Indisponível").append(", ")
                .append("Tarde: ").append(dailyAvailability.isAfternoonAvailable() ? "Disponível" : "Indisponível").append(", ")
                .append("Noite: ").append(dailyAvailability.isEveningAvailable() ? "Disponível" : "Indisponível").append("\n");
        }
        return availability.toString();
    }

    // --- TEMPLATE METHOD para geração da rotina (usando Strategy)
    public String generateRoutine(Long routineId, String... feedback) {
        T routine = findById(routineId)
            .orElseThrow(() -> new RoutineNotFoundException("Rotina não encontrada com ID: " + routineId));

        generationStrategy.validateRoutineForGeneration(routine);
        generationStrategy.saveHistory(routine);
        String availabilityString = getWeekAvailabilityString(routine);
        String prompt = generationStrategy.buildGenerationPrompt(routine, availabilityString, feedback);

        String generatedText = fachadaLLM.chat(prompt);
        routine.setGeneratedRoutine(generatedText);
        routineRepository.save(routine);

        generationStrategy.sendSuccessNotification(routine);

        return generatedText;
    }

    // Cada tipo concreto ainda precisa dizer como buscar o histórico
    public abstract List<RoutineHistory> getRoutineHistory();

    // GETTERS e SETTERS
    public R getRoutineRepository() {
        return routineRepository;
    }

    public void setRoutineRepository(R routineRepository) {
        this.routineRepository = routineRepository;
    }

    public DailyAvailabilityRepository getDailyAvailabilityRepository() {
        return dailyAvailabilityRepository;
    }

    public void setDailyAvailabilityRepository(DailyAvailabilityRepository dailyAvailabilityRepository) {
        this.dailyAvailabilityRepository = dailyAvailabilityRepository;
    }

    public SecurityUtils getSecurityUtils() {
        return securityUtils;
    }

    public void setSecurityUtils(SecurityUtils securityUtils) {
        this.securityUtils = securityUtils;
    }

    public FachadaLLM getFachadaLLM() {
        return fachadaLLM;
    }

    public void setFachadaLLM(FachadaLLM fachadaLLM) {
        this.fachadaLLM = fachadaLLM;
    }

    public NotificationService getNotificationService() {
        return notificationService;
    }

    public void setNotificationService(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public RoutineGenerationStrategy<T> getGenerationStrategy() {
        return generationStrategy;
    }
}
