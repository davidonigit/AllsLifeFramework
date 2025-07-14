package com.grupo3.allslife_framework.language.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.grupo3.allslife_framework.framework.controller.AbstractGenericController;
import com.grupo3.allslife_framework.language.dto.LanguageRoutineDTO;
import com.grupo3.allslife_framework.language.model.LanguageRoutine;
import com.grupo3.allslife_framework.language.service.LanguageRoutineService;

@RestController
@RequestMapping("/api/language-routine")
public class LanguageRoutineController extends AbstractGenericController<
    LanguageRoutine,
    LanguageRoutineDTO,
    LanguageRoutineService
> {

    public LanguageRoutineController(LanguageRoutineService service) {
        super(service);
    }

    @Override
    public ResponseEntity<LanguageRoutine> getMyData() {
        return ResponseEntity.ok(service.getOrCreateLanguageRoutineForCurrentUser());
    }

    @Override
    public ResponseEntity<LanguageRoutine> updateMyData(@RequestBody LanguageRoutineDTO body) {
        return ResponseEntity.ok(service.updateLanguageRoutine(body));
    }
}
