package com.grupo3.allslife_framework.framework.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.grupo3.allslife_framework.framework.model.AbstractRoutine;
import com.grupo3.allslife_framework.framework.model.RoutineHistory;
import com.grupo3.allslife_framework.framework.service.AbstractRoutineService;

public abstract class AbstractRoutineController<
    T extends AbstractRoutine,
    S extends AbstractRoutineService<T, ?>
> {

    protected final S routineService;

    protected AbstractRoutineController(S routineService) {
        this.routineService = routineService;
    }

    @GetMapping
    public ResponseEntity<T> getMyRoutine() {
        return ResponseEntity.ok(routineService.findByCurrentUser());
    }

    @GetMapping("/generate")
    public ResponseEntity<String> generateRoutine() {
        T routine = routineService.findByCurrentUser();
        String routineText = routineService.generateRoutine(routine.getId());
        return ResponseEntity.ok(routineText);
    }

    @PutMapping("/feedback")
    public ResponseEntity<String> generateRoutineWithFeedback(@RequestBody String feedback) {
        T routine = routineService.findByCurrentUser();
        String routineText = routineService.generateRoutine(routine.getId(), feedback);
        return ResponseEntity.ok(routineText);
    }

    @GetMapping("/history")
    public ResponseEntity<List<RoutineHistory>> getRoutineHistory() {
        return ResponseEntity.ok(routineService.getRoutineHistory());
    }
}
