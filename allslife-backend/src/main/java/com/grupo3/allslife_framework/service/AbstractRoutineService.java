package com.grupo3.allslife_framework.service;

import lombok.AllArgsConstructor;

import java.util.Optional;

import com.grupo3.allslife_framework.enums.DayOfWeekEnum;
import com.grupo3.allslife_framework.exception.RoutineNotFoundException;
import com.grupo3.allslife_framework.exception.UserNotFoundException;
import com.grupo3.allslife_framework.model.AbstractRoutine;
import com.grupo3.allslife_framework.model.DailyAvailability;
import com.grupo3.allslife_framework.repository.AbstractRoutineRepository;
import com.grupo3.allslife_framework.repository.DailyAvailabilityRepository;
import com.grupo3.allslife_framework.security.SecurityUtils;

/**
 * Serviço base abstrato que define o fluxo de operações para qualquer tipo de rotina.
 * Utiliza o padrão Template Method para delegar passos específicos às subclasses.
 *
 * @param <T> O tipo da entidade de rotina.
 * @param <R> O tipo do repositório para a rotina.
 */
@AllArgsConstructor
public abstract class AbstractRoutineService<
    T extends AbstractRoutine,
    R extends AbstractRoutineRepository<T>
> {

    protected final R routineRepository;
    protected final DailyAvailabilityRepository dailyAvailabilityRepository;
    protected final SecurityUtils securityUtils;
    protected final FachadaLLM fachadaLLM;
    protected final NotificationService notificationService;
    protected final UserService userService;

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

    // --- TEMPLATE METHOD PARA GERAÇÃO DA ROTINA ---

    public String generateRoutine(Long routineId, String... feedback) {
        T routine = findById(routineId)
            .orElseThrow(() -> new RoutineNotFoundException("Rotina não encontrada com ID: " + routineId));

        // 1. Validar se a rotina pode ser gerada
        validateRoutineForGeneration(routine);

        // 2. Salvar histórico, se aplicável (passo abstrato)
        saveHistory(routine);
        
        // 3. Montar o prompt (passo abstrato)
        String prompt = buildGenerationPrompt(routine, feedback);

        // 4. Chamar a LLM
        String generatedText = fachadaLLM.chat(prompt);
        routine.setGeneratedRoutine(generatedText);
        routineRepository.save(routine);

        // 5. Enviar notificação (passo abstrato para permitir customização da msg)
        sendSuccessNotification(routine);
        
        return generatedText;
    }


    // MÉTODOS ABSTRATOS (A SEREM IMPLEMENTADOS PELAS SUBCLASSES)

    /**
     * Valida se a rotina tem os dados específicos necessários para a geração.
     * Ex: SportRoutine precisa ter um sportName.
     */
    protected abstract void validateRoutineForGeneration(T routine);

    /**
     * Constrói o prompt específico para o contexto da rotina.
     */
    protected abstract String buildGenerationPrompt(T routine, String... feedback);
    
    /**
     * Salva a rotina atual no histórico específico.
     */
    protected abstract void saveHistory(T routine);

    /**
     * Envia uma notificação de sucesso com uma mensagem customizada.
     */
    protected abstract void sendSuccessNotification(T routine);
}
