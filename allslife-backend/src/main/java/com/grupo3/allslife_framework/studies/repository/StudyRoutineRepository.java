package com.grupo3.allslife_framework.studies.repository; // No pacote da aplicação

import org.springframework.stereotype.Repository;

import com.grupo3.allslife_framework.framework.repository.AbstractRoutineRepository;
import com.grupo3.allslife_framework.studies.model.StudyRoutine;

@Repository
public interface StudyRoutineRepository extends AbstractRoutineRepository<StudyRoutine> {
}

