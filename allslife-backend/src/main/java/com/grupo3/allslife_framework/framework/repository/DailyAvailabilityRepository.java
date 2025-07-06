package com.grupo3.allslife_framework.framework.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.grupo3.allslife_framework.framework.model.DailyAvailability;

@Repository
public interface DailyAvailabilityRepository extends JpaRepository<DailyAvailability, Long> {
    List<DailyAvailability> findByRoutineId(Long routineId);
}
