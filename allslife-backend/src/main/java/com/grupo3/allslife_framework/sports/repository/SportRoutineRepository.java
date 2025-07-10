package com.grupo3.allslife_framework.sports.repository; // No pacote da aplicação

import org.springframework.stereotype.Repository;

import com.grupo3.allslife_framework.framework.repository.AbstractRoutineRepository;
import com.grupo3.allslife_framework.sports.model.SportRoutine;

@Repository
public interface SportRoutineRepository extends AbstractRoutineRepository<SportRoutine> {
}