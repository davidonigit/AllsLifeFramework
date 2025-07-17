package com.grupo3.allslife_framework.studies.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.grupo3.allslife_framework.framework.controller.AbstractGenericController;
import com.grupo3.allslife_framework.studies.dto.StudyUserPreferencesDTO;
import com.grupo3.allslife_framework.studies.model.StudyUserPreferences;
import com.grupo3.allslife_framework.studies.service.StudyRoutineService;

@RestController
@RequestMapping("/api/study-preferences")
public class StudyUserPreferencesController extends AbstractGenericController<
    StudyUserPreferences,
    StudyUserPreferencesDTO,
    StudyRoutineService
> {

    public StudyUserPreferencesController(StudyRoutineService service) {
        super(service);
    }

    @Override
    public ResponseEntity<StudyUserPreferences> getMyData() {
        return ResponseEntity.ok(service.getOrCreateStudyPreferences());
    }

    @Override
    public ResponseEntity<StudyUserPreferences> updateMyData(@RequestBody StudyUserPreferencesDTO body) {
        return ResponseEntity.ok(service.updateStudyUserPreferences(body));
    }
}


