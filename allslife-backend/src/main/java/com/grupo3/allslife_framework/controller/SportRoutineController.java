package com.grupo3.allslife_framework.controller;


import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.grupo3.allslife_framework.dto.SportRoutineDTO;
import com.grupo3.allslife_framework.model.RoutineHistory;
import com.grupo3.allslife_framework.model.SportRoutine;
import com.grupo3.allslife_framework.service.SportRoutineService;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;


@Controller
@RequestMapping("/api/sport-routine")
@AllArgsConstructor
public class SportRoutineController {
    
    private final SportRoutineService sportRoutineService;
    
    @GetMapping
    public ResponseEntity<SportRoutine> getMyRoutine() {
        return ResponseEntity.ok(sportRoutineService.findByCurrentUser());
    }


    @GetMapping("/generate")
    public ResponseEntity<String> generateSportRoutine() {
        SportRoutine sportRoutine = sportRoutineService.findByCurrentUser();
        String routine = sportRoutineService.generateRoutine(sportRoutine.getId());

        return ResponseEntity.ok(routine);
    }

    
   @PutMapping("/feedback")
    public ResponseEntity<String> generateSportRoutineWithFeedback(@RequestBody String feedback) {
        SportRoutine sportRoutine = sportRoutineService.findByCurrentUser();
        String routine = sportRoutineService.generateRoutine(sportRoutine.getId(), feedback);

        return ResponseEntity.ok(routine);
    }
    
    
    @PutMapping
    public ResponseEntity<SportRoutine> updateSportRoutine(@RequestBody SportRoutineDTO body){
        return ResponseEntity.ok(sportRoutineService.updateSportRoutine(body));
    }

    @GetMapping("/history")
    public ResponseEntity<List<RoutineHistory>> getSportRoutineHistory() {
        return ResponseEntity.ok(sportRoutineService.getSportRoutineHistory());
    }
    
}
