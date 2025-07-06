package com.grupo3.allslife_framework.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.grupo3.allslife_framework.dto.SportUserPreferencesDTO;
import com.grupo3.allslife_framework.model.SportUserPreferences;
import com.grupo3.allslife_framework.service.SportRoutineService;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;


@Controller
@RequestMapping("/api/user-preferences")
@AllArgsConstructor
public class SportUserPreferencesController {
    
	@Autowired
    private SportRoutineService sportRoutineService;

    @GetMapping
    public ResponseEntity<SportUserPreferences> getMySportUserPreferences() {
        return ResponseEntity.ok(sportRoutineService.getOrCreateSportPreferences());
    }

    @PutMapping
    public ResponseEntity<SportUserPreferences> updateMySportUserPreferences(@RequestBody SportUserPreferencesDTO body) {
        return ResponseEntity.ok(sportRoutineService.updateSportUserPreferences(body));
    }
}
