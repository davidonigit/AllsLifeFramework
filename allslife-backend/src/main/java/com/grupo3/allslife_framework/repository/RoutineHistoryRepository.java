package com.grupo3.allslife_framework.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.grupo3.allslife_framework.model.RoutineHistory;

@Repository
public interface RoutineHistoryRepository extends JpaRepository <RoutineHistory, Long>{
    List<RoutineHistory> findByUserId(Long usedId);
}
