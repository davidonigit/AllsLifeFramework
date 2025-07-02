package com.grupo3.allslife_framework.service;

import com.grupo3.allslife_framework.dto.NotificationDTO;
import com.grupo3.allslife_framework.dto.SportRoutineDTO;
import com.grupo3.allslife_framework.exception.RoutineNotFoundException;
import com.grupo3.allslife_framework.model.RoutineHistory;
import com.grupo3.allslife_framework.model.SportRoutine;
import com.grupo3.allslife_framework.repository.DailyAvailabilityRepository;
import com.grupo3.allslife_framework.repository.RoutineHistoryRepository;
import com.grupo3.allslife_framework.repository.SportRoutineRepository;
import com.grupo3.allslife_framework.security.SecurityUtils;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class SportRoutineService extends AbstractRoutineService<SportRoutine, SportRoutineRepository> {

    // Dependências específicas desta implementação
    private final RoutineHistoryRepository RoutineHistoryRepository;

    // Construtor que injeta as dependências e as passa para a classe pai
    public SportRoutineService(SportRoutineRepository sportRoutineRepository,
                               DailyAvailabilityRepository dailyAvailabilityRepository,
                               SecurityUtils securityUtils,
                               FachadaLLM fachadaLLM,
                               NotificationService notificationService,
                               RoutineHistoryRepository RoutineHistoryRepository) {
        super(sportRoutineRepository, dailyAvailabilityRepository, securityUtils, fachadaLLM, notificationService);
        this.RoutineHistoryRepository = RoutineHistoryRepository;
    }

    // --- IMPLEMENTAÇÃO DOS MÉTODOS ABSTRATOS ---

    @Override
    protected void validateRoutineForGeneration(SportRoutine routine) {
        System.out.println("ROTINA: " + routine);
        System.out.println("DISPONIBILIDADE: " + getWeekAvailabilityString(routine));

        if (routine.getSportName() == null || routine.getSportName().isEmpty()) {
            throw new IllegalArgumentException("Esporte não definido para a rotina.");
        }
        if (!getWeekAvailabilityString(routine).contains("Disponível")) {
            throw new IllegalArgumentException("A rotina não possui horários disponíveis.");
        }
    }
    
    @Override
    protected void saveHistory(SportRoutine routine) {
        if(routine.getGeneratedRoutine() != null) {
            RoutineHistory history = new RoutineHistory();
            history.setGeneratedRoutine(routine.getGeneratedRoutine());
            history.setSportName(routine.getSportName());
            history.setUser(routine.getUser());
            RoutineHistoryRepository.save(history);
        }
    }

    @Override
    protected String buildGenerationPrompt(SportRoutine routine, String... feedback) {
        String basePrompt = "Responda como um especialista em esportes. " +
                "Baseado no esporte: " + routine.getSportName() +
                ", gere uma rotina de treino personalizada para o usuário, " +
                "especializado para o esporte desejado. " +
                "A rotina deve preencher os seguintes dias e horários disponíveis: " +
               getWeekAvailabilityString(routine) +
               "Retorne apenas a rotina de treino, sem explicações adicionais. " +
                "Caso necessário, cria uma seção com informações importantes no inicio. " +
                "Após isso, inclua um título 'Rotina de Treino Personalizada para " + routine.getSportName() +
                "A rotina deve ser formatada em Markdown, dando maior destaque aos dias da semana. " +
                "Sempre use os nomes dos dias da semana em português, começando pelo domingo. " +
                "Crie uma separação no texto entre os dias para facilitar a leitura. " +
                "Adicione a separação entre as informações importantes e a rotina.";
        
        if (feedback != null && feedback.length > 0 && !feedback[0].isEmpty()) {
            basePrompt += " Leve em consideração o feedback do usuário: " + feedback[0];
        }
        
        return basePrompt;
    }

    @Override
    protected void sendSuccessNotification(SportRoutine routine) {
        NotificationDTO dto = new NotificationDTO("Rotina de Esporte Gerada!",
                "Sua rotina de treino para " + routine.getSportName() + " foi gerada. Confira!",
                routine.getUser().getId());
        notificationService.create(dto);
    }


    // --- MÉTODOS ESPECÍFICOS DE sportRoutineService ---

    public SportRoutine updateSportRoutine(SportRoutineDTO dto) {
        SportRoutine sportRoutine = findByCurrentUser();
        if (sportRoutine == null) {
            throw new RoutineNotFoundException("Rotina esportiva não encontrada para o usuário atual.");
        }

        sportRoutine.setSportName(dto.sport());

        // Atualizar a disponibilidade semanal
        for (var dailyAvailability : dto.weeklyAvailability()) {
            sportRoutine.updateAvailability(
                dailyAvailability.dayOfWeek(),
                dailyAvailability.morningAvailable(),
                dailyAvailability.afternoonAvailable(),
                dailyAvailability.eveningAvailable()
            );
        }
        
        return routineRepository.save(sportRoutine);
    }
    
    public List<RoutineHistory> getSportRoutineHistory() {
        Long userId = securityUtils.getCurrentUserId();
        // ...lógica para buscar histórico
        return RoutineHistoryRepository.findByUserId(userId);
    }
    
}