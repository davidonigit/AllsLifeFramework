package com.grupo3.allslife_app.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.grupo3.allslife_app.dto.NotificationDTO;
import com.grupo3.allslife_app.dto.SportRoutineDTO;
import com.grupo3.allslife_app.enums.DayOfWeekEnum;
import com.grupo3.allslife_app.exception.SportRoutineNotFoundException;
import com.grupo3.allslife_app.exception.UserNotFoundException;
import com.grupo3.allslife_app.model.DailyAvailability;
import com.grupo3.allslife_app.model.SportRoutine;
import com.grupo3.allslife_app.model.SportRoutineHistory;
import com.grupo3.allslife_app.repository.DailyAvailabilityRepository;
import com.grupo3.allslife_app.repository.SportRoutineHistoryRepository;
import com.grupo3.allslife_app.repository.SportRoutineRepository;
import com.grupo3.allslife_app.security.SecurityUtils;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@Transactional
@AllArgsConstructor
public class SportRoutineService {
    
    private final SportRoutineRepository sportRoutineRepository;
    private final SportRoutineHistoryRepository sportRoutineHistoryRepository;
    private final DailyAvailabilityRepository dailyAvailabilityRepository;
    private final NotificationService notificationService;
    private final FachadaLLM fachadaLLM;
    private final SecurityUtils securityUtils;
    

    public Optional<SportRoutine> findById(Long id) {
        return sportRoutineRepository.findById(id);
    }
    

    public SportRoutine findByUserId() {
        Long userId = securityUtils.getCurrentUserId();
        if (userId == null) {
            throw new UserNotFoundException("No user logged in");
        }
        return sportRoutineRepository.findByUserId(userId).orElseThrow(() -> new SportRoutineNotFoundException("Rotina esportiva não encontrada para o usuário com ID: " + userId));
    }


    public SportRoutine updateSportName(Long routineId, String sportName) {
        SportRoutine sportRoutine = sportRoutineRepository.findById(routineId)
            .orElseThrow(() -> new SportRoutineNotFoundException("Rotina esportiva não encontrada com ID: " + routineId));
        
        sportRoutine.setSportName(sportName);
        return sportRoutineRepository.save(sportRoutine);
    }


    public SportRoutine initializeWeeklyAvailability(Long routineId) {
        SportRoutine sportRoutine = sportRoutineRepository.findById(routineId)
            .orElseThrow(() -> new SportRoutineNotFoundException("Rotina esportiva não encontrada com ID: " + routineId));
        
        sportRoutine.getWeeklyAvailability().clear();
        
        for (DayOfWeekEnum day : DayOfWeekEnum.values()) {
            DailyAvailability availability = new DailyAvailability();
            availability.setDayOfWeek(day);
            availability.setMorningAvailable(false);
            availability.setAfternoonAvailable(false);
            availability.setEveningAvailable(false);
            availability.setSportRoutine(sportRoutine);
            
            sportRoutine.getWeeklyAvailability().add(availability);
            dailyAvailabilityRepository.save(availability);
        }

        return sportRoutineRepository.save(sportRoutine);
    }
    

    public SportRoutine updateDailyAvailability(
            Long routineId, 
            DayOfWeekEnum day, 
            boolean morning, 
            boolean afternoon, 
            boolean evening) {
        
        SportRoutine sportRoutine = sportRoutineRepository.findById(routineId)
                .orElseThrow(() -> new SportRoutineNotFoundException("Rotina esportiva não encontrada com ID: " + routineId));
        
        sportRoutine.updateAvailability(day, morning, afternoon, evening);
        return sportRoutineRepository.save(sportRoutine);
    }
    

    public DailyAvailability getDailyAvailability(Long routineId, DayOfWeekEnum day) {
        SportRoutine sportRoutine = sportRoutineRepository.findById(routineId)
                .orElseThrow(() -> new SportRoutineNotFoundException("Rotina esportiva não encontrada com ID: " + routineId));
        
        DailyAvailability availability = sportRoutine.getAvailabilityForDay(day);
        if (availability == null) {
            throw new EntityNotFoundException("Disponibilidade não encontrada para o dia: " + day);
        }
        
        return availability;
    }


    public SportRoutine saveSportRoutine(SportRoutine sportRoutine){
        return sportRoutineRepository.save(sportRoutine);
    }

    private String weekAvailabilityString(SportRoutine sportRoutine) {
        StringBuilder availability = new StringBuilder();
        for (DailyAvailability dailyAvailability : sportRoutine.getWeeklyAvailability()) {
            availability.append(dailyAvailability.getDayOfWeek())
                .append(": ")
                .append("Manhã: ").append(dailyAvailability.isMorningAvailable() ? "Disponível" : "Indisponível").append(", ")
                .append("Tarde: ").append(dailyAvailability.isAfternoonAvailable() ? "Disponível" : "Indisponível").append(", ")
                .append("Noite: ").append(dailyAvailability.isEveningAvailable() ? "Disponível" : "Indisponível").append("\n");
        }
        return availability.toString();
    }


    public String generateSportRoutine(Long routineId) {
        SportRoutine sportRoutine = sportRoutineRepository.findById(routineId)
            .orElseThrow(() -> new SportRoutineNotFoundException("Rotina esportiva nao encontrada com ID: " + routineId));
        
        if (sportRoutine.getSportName() == null || sportRoutine.getSportName().isEmpty()) {
            throw new IllegalArgumentException("Esporte nao definido para a rotina.");
        }

        String availabilityString = weekAvailabilityString(sportRoutine);
        if (!availabilityString.contains("Disponível")) {	
            throw new IllegalArgumentException("A rotina nao possui horarios disponiveis.");
        }

        if(sportRoutine.getGeneratedRoutine() != null) {
            saveSportRoutineHistory(sportRoutine);
        }
        
        String prompt = "Responda como um especialista em esportes. " +
                "Baseado no esporte: " + sportRoutine.getSportName() +
                ", gere uma rotina de treino personalizada para o usuário, " +
                "especializado para o esporte desejado. " +
                "A rotina deve preencher os seguintes dias e horários disponíveis: " +
               weekAvailabilityString(sportRoutine) +
               "Retorne apenas a rotina de treino, sem explicações adicionais. " +
                "Caso necessário, cria uma seção com informações importantes no inicio. " +
                "Após isso, inclua um título 'Rotina de Treino Personalizada para " + sportRoutine.getSportName() +
                "A rotina deve ser formatada em Markdown, dando maior destaque aos dias da semana. " +
                "Sempre use os nomes dos dias da semana em português, começando pelo domingo. " +
                "Crie uma separação no texto entre os dias para facilitar a leitura. " +
                "Adicione a separação entre as informações importantes e a rotina.";

        String routine = fachadaLLM.chat(prompt);

        sportRoutine.setGeneratedRoutine(routine);
        sportRoutineRepository.save(sportRoutine);
        NotificationDTO notificationDTO = new NotificationDTO("Rotina gerada com sucesso!", "A sua rotina de treino para o esporte " + sportRoutine.getSportName() + " foi gerada com sucesso. Parabéns, meta marcha em seguir o treinamento e seja feliz, stay alive " + sportRoutine.getUser().getName() + ".", sportRoutine.getUser().getId());
        notificationService.create(notificationDTO);
        return routine;
    }


    public String generateSportRoutineWithFeedback(Long routineId, String feedback) {
        SportRoutine sportRoutine = sportRoutineRepository.findById(routineId)
            .orElseThrow(() -> new SportRoutineNotFoundException("Rotina esportiva nao encontrada com ID: " + routineId));
        
        if (sportRoutine.getSportName() == null || sportRoutine.getSportName().isEmpty()) {
            throw new IllegalArgumentException("Esporte nao definido para a rotina.");
        }

        String availabilityString = weekAvailabilityString(sportRoutine);
        if (!availabilityString.contains("Disponível")) {	
            throw new IllegalArgumentException("A rotina nao possui horarios disponiveis.");
        }

        if(sportRoutine.getGeneratedRoutine() != null) {
            saveSportRoutineHistory(sportRoutine);
        }
        
        String prompt = "Responda como um especialista em esportes. " +
                "Baseado no esporte: " + sportRoutine.getSportName() +
                ", gere uma rotina de treino personalizada para o usuário, " +
                "especializado para o esporte desejado. " +
                "A rotina deve preencher os seguintes dias e horários disponíveis: " +
               weekAvailabilityString(sportRoutine) +
               "Leve em consideração o feedback do usuário na construção da rotina: " + feedback + ". " +
               "Retorne apenas a rotina de treino, sem explicações adicionais. " +
                "Caso necessário, cria uma seção com informações importantes no inicio. " +
                "Após isso, inclua um título 'Rotina de Treino Personalizada para " + sportRoutine.getSportName() +
                "A rotina deve ser formatada em Markdown, dando maior destaque aos dias da semana. " +
                "Sempre use os nomes dos dias da semana em português, começando pelo domingo. " +
                "Crie uma separação no texto entre os dias para facilitar a leitura. " +
                "Adicione a separação entre as informações importantes e a rotina.";

        String routine = fachadaLLM.chat(prompt);

        sportRoutine.setGeneratedRoutine(routine);
        sportRoutineRepository.save(sportRoutine);
        NotificationDTO notificationDTO = new NotificationDTO("Rotina gerada com sucesso!", "A sua rotina de treino para o esporte " + sportRoutine.getSportName() + " foi gerada com sucesso. Parabéns, meta marcha em seguir o treinamento e seja feliz, stay alive " + sportRoutine.getUser().getName() + ".", sportRoutine.getUser().getId());
        notificationService.create(notificationDTO);
        return routine;
    }

    private void saveSportRoutineHistory(SportRoutine sportRoutine){
        SportRoutineHistory history = new SportRoutineHistory();
        history.setGeneratedRoutine(sportRoutine.getGeneratedRoutine());
        history.setSportName(sportRoutine.getSportName());
        history.setUser(sportRoutine.getUser());
        sportRoutineHistoryRepository.save(history);
    }

    public List<SportRoutineHistory> getSportRoutineHistory() {
        Long userId = securityUtils.getCurrentUserId();
        if (userId == null) {
            throw new UserNotFoundException("No user logged in");
        }
        return sportRoutineHistoryRepository.findByUserId(userId);
    }

    public SportRoutine updateSportRoutine(SportRoutineDTO dto) {
        SportRoutine sportRoutine = findByUserId();
        
        updateSportName(sportRoutine.getId(), dto.sport());

        for (var dailyAvailability : dto.weeklyAvailability()) {
            sportRoutine.updateAvailability(
                dailyAvailability.dayOfWeek(),
                dailyAvailability.morningAvailable(),
                dailyAvailability.afternoonAvailable(),
                dailyAvailability.eveningAvailable()
            );
        }
        
        saveSportRoutine(sportRoutine);
        return sportRoutineRepository.save(sportRoutine);
    }
}
