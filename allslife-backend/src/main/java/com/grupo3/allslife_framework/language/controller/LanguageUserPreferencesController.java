package com.grupo3.allslife_framework.language.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.grupo3.allslife_framework.framework.controller.AbstractGenericController;
import com.grupo3.allslife_framework.language.dto.LanguageUserPreferencesDTO;
import com.grupo3.allslife_framework.language.model.LanguageUserPreferences;
import com.grupo3.allslife_framework.language.service.LanguageRoutineService;

@RestController
@RequestMapping("/api/language-preferences")
public class LanguageUserPreferencesController extends AbstractGenericController<
    LanguageUserPreferences,
    LanguageUserPreferencesDTO,
    LanguageRoutineService
> {

    public LanguageUserPreferencesController(LanguageRoutineService service) {
        super(service);
    }

    @Override
    public ResponseEntity<LanguageUserPreferences> getMyData() {
        return ResponseEntity.ok(service.getOrCreateLanguagePreferences());
    }

    @Override
    public ResponseEntity<LanguageUserPreferences> updateMyData(@RequestBody LanguageUserPreferencesDTO body) {
        return ResponseEntity.ok(service.updateLanguageUserPreferences(body));
    }
}
