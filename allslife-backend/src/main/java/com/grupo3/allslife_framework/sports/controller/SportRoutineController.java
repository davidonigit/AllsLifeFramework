package com.grupo3.allslife_framework.sports.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.grupo3.allslife_framework.framework.controller.AbstractRoutineController;
import com.grupo3.allslife_framework.sports.dto.SportRoutineDTO;
import com.grupo3.allslife_framework.sports.model.SportRoutine;
import com.grupo3.allslife_framework.sports.service.SportRoutineService;

@RestController
@RequestMapping("/api/sport-routine")
public class SportRoutineController extends AbstractRoutineController<SportRoutine, SportRoutineService> {

    private final SportRoutineService sportRoutineService;
    
    public SportRoutineController(SportRoutineService sportRoutineService) {
        super(sportRoutineService);
        this.sportRoutineService = sportRoutineService;
    }

    @PutMapping
    public ResponseEntity<SportRoutine> updateSportRoutine(@RequestBody SportRoutineDTO dto) {
        return ResponseEntity.ok(sportRoutineService.updateSportRoutine(dto));
    }
}
