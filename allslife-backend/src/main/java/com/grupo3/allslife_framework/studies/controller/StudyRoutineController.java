package com.grupo3.allslife_framework.studies.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.grupo3.allslife_framework.framework.controller.AbstractGenericController;
import com.grupo3.allslife_framework.studies.dto.StudyRoutineDTO;
import com.grupo3.allslife_framework.studies.model.StudyRoutine;
import com.grupo3.allslife_framework.studies.service.StudyRoutineService;

@RestController
@RequestMapping("/api/study-routine")
public class StudyRoutineController extends AbstractGenericController<
    StudyRoutine,
    StudyRoutineDTO,
    StudyRoutineService
> {

    public StudyRoutineController(StudyRoutineService service) {
        super(service);
    }

    @Override
    public ResponseEntity<StudyRoutine> getMyData() {
        return ResponseEntity.ok(service.findByCurrentUser());
    }

    @Override
    public ResponseEntity<StudyRoutine> updateMyData(@RequestBody StudyRoutineDTO body) {
        return ResponseEntity.ok(service.updateStudyRoutine(body));
    }
}


