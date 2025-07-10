package com.grupo3.allslife_framework.sports.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.grupo3.allslife_framework.framework.controller.AbstractGenericController;
import com.grupo3.allslife_framework.sports.dto.SportUserPreferencesDTO;
import com.grupo3.allslife_framework.sports.model.SportUserPreferences;
import com.grupo3.allslife_framework.sports.service.SportRoutineService;

@RestController
@RequestMapping("/api/sport-preferences")
public class SportUserPreferencesController extends AbstractGenericController<
    SportUserPreferences,
    SportUserPreferencesDTO,
    SportRoutineService
> {

    public SportUserPreferencesController(SportRoutineService service) {
        super(service);
    }

    @Override
    public ResponseEntity<SportUserPreferences> getMyData() {
        return ResponseEntity.ok(service.getOrCreateSportPreferences());
    }

    @Override
    public ResponseEntity<SportUserPreferences> updateMyData(@RequestBody SportUserPreferencesDTO body) {
        return ResponseEntity.ok(service.updateSportUserPreferences(body));
    }
}
