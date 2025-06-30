package com.grupo3.allslife_app.controller;


import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.grupo3.allslife_app.dto.SportRoutineDTO;
import com.grupo3.allslife_app.model.SportRoutine;
import com.grupo3.allslife_app.model.SportRoutineHistory;
import com.grupo3.allslife_app.service.SportRoutineService;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;


@Controller
@RequestMapping("/api/sport-routine")
@AllArgsConstructor
public class SportRoutineController {
    
    private final SportRoutineService sportRoutineService;
    
    @GetMapping
    public ResponseEntity<SportRoutine> getMyRoutine() {
        return ResponseEntity.ok(sportRoutineService.findByUserId());
    }


    @GetMapping("/generate")
    public ResponseEntity<String> generateSportRoutine() {
        SportRoutine sportRoutine = sportRoutineService.findByUserId();
        String routine = sportRoutineService.generateSportRoutine(sportRoutine.getId());

        return ResponseEntity.ok(routine);
    }

    
   @PutMapping("/feedback")
    public ResponseEntity<String> generateSportRoutineWithFeedback(@RequestBody String feedback) {
        SportRoutine sportRoutine = sportRoutineService.findByUserId();
        String routine = sportRoutineService.generateSportRoutineWithFeedback(sportRoutine.getId(), feedback);

        return ResponseEntity.ok(routine);
    }
    
    
    @PutMapping
    public ResponseEntity<SportRoutine> updateSportRoutine(@RequestBody SportRoutineDTO body){
        return ResponseEntity.ok(sportRoutineService.updateSportRoutine(body));
    }

    @GetMapping("/history")
    public ResponseEntity<List<SportRoutineHistory>> getSportRoutineHistory() {
        return ResponseEntity.ok(sportRoutineService.getSportRoutineHistory());
    }
    
}
