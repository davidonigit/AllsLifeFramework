package com.grupo3.allslife_app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.grupo3.allslife_app.model.GoalBoard;

@Repository
public interface GoalBoardRepository extends JpaRepository<GoalBoard, Long> {
     Optional<GoalBoard> findByUserId(Long usedId);
}