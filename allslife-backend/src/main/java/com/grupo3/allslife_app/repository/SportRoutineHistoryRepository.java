package com.grupo3.allslife_app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.grupo3.allslife_app.model.SportRoutineHistory;

@Repository
public interface SportRoutineHistoryRepository extends JpaRepository <SportRoutineHistory, Long>{
    List<SportRoutineHistory> findByUserId(Long usedId);
}
