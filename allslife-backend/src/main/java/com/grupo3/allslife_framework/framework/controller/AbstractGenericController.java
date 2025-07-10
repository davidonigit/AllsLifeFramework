package com.grupo3.allslife_framework.framework.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.grupo3.allslife_framework.framework.enums.DayOfWeekEnum;
import com.grupo3.allslife_framework.framework.model.AbstractRoutine;
import com.grupo3.allslife_framework.framework.model.RoutineHistory;
import com.grupo3.allslife_framework.framework.service.AbstractRoutineService;

/**
 * Controller abstrato que atende tanto rotinas quanto user preferences.
 *
 * @param <R> Tipo da entidade (AbstractRoutine ou AbstractUserPreferences)
 * @param <DTO> Tipo do DTO usado para atualização
 * @param <S> Tipo do service concreto
 */
public abstract class AbstractGenericController<
    R,
    DTO,
    S extends AbstractRoutineService<?, ?>
> {

    protected final S service;

    protected AbstractGenericController(S service) {
        this.service = service;
    }

    /**
     * GET - Buscar entidade do usuário atual (rotina ou preferência)
     */
    @GetMapping
    public abstract ResponseEntity<R> getMyData();

    /**
     * PUT - Atualizar entidade do usuário atual
     */
    @PutMapping
    public abstract ResponseEntity<R> updateMyData(@RequestBody DTO body);

    // --- Métodos específicos para rotinas (opcionais nas controllers concretas) ---

    @GetMapping("/generate")
    public ResponseEntity<String> generateRoutine() {
        var routine = service.findByCurrentUser();
        String result = service.generateRoutine(routine.getId());
        return ResponseEntity.ok(result);
    }

    @PutMapping("/feedback")
    public ResponseEntity<String> generateRoutineWithFeedback(@RequestBody String feedback) {
        var routine = service.findByCurrentUser();
        String result = service.generateRoutine(routine.getId(), feedback);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/history")
    public ResponseEntity<List<RoutineHistory>> getRoutineHistory() {
        return ResponseEntity.ok(service.getRoutineHistory());
    }

    @PutMapping("/availability")
    public ResponseEntity<AbstractRoutine> updateDailyAvailability(
            @RequestParam Long routineId,
            @RequestParam DayOfWeekEnum day,
            @RequestParam boolean morning,
            @RequestParam boolean afternoon,
            @RequestParam boolean evening
    ) {
        AbstractRoutine updated = service.updateDailyAvailability(routineId, day, morning, afternoon, evening);
        return ResponseEntity.ok(updated);
    }
}
