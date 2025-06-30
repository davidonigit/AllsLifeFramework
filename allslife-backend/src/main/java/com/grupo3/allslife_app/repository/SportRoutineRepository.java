package com.grupo3.allslife_app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.grupo3.allslife_app.model.SportRoutine;

@Repository
public interface SportRoutineRepository extends JpaRepository <SportRoutine, Long>{
    Optional<SportRoutine> findByUserId(Long usedId);
}
