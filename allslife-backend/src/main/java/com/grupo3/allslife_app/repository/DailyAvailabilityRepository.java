package com.grupo3.allslife_app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.grupo3.allslife_app.model.DailyAvailability;

@Repository
public interface DailyAvailabilityRepository extends JpaRepository<DailyAvailability, Long> {
    List<DailyAvailability> findBySportRoutineId(Long sportRoutineId);
}
