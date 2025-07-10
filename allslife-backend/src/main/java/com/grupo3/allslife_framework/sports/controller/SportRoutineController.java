package com.grupo3.allslife_framework.sports.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.grupo3.allslife_framework.framework.controller.AbstractGenericController;
import com.grupo3.allslife_framework.sports.dto.SportRoutineDTO;
import com.grupo3.allslife_framework.sports.model.SportRoutine;
import com.grupo3.allslife_framework.sports.service.SportRoutineService;

@RestController
@RequestMapping("/api/sport-routine")
public class SportRoutineController extends AbstractGenericController<
    SportRoutine,
    SportRoutineDTO,
    SportRoutineService
> {

    public SportRoutineController(SportRoutineService service) {
        super(service);
    }

    @Override
    public ResponseEntity<SportRoutine> getMyData() {
        return ResponseEntity.ok(service.findByCurrentUser());
    }

    @Override
    public ResponseEntity<SportRoutine> updateMyData(@RequestBody SportRoutineDTO body) {
        return ResponseEntity.ok(service.updateSportRoutine(body));
    }
}
