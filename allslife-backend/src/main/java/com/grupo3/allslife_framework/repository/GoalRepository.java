package com.grupo3.allslife_framework.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.grupo3.allslife_framework.model.Goal;

@Repository
public interface GoalRepository extends JpaRepository<Goal, Long>{

    
}
