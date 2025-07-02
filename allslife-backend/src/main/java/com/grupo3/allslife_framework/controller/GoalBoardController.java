package com.grupo3.allslife_framework.controller;

import org.springframework.web.bind.annotation.RestController;

import com.grupo3.allslife_framework.dto.GoalDTO;
import com.grupo3.allslife_framework.model.Goal;
import com.grupo3.allslife_framework.model.GoalBoard;
import com.grupo3.allslife_framework.service.GoalBoardService;

import lombok.AllArgsConstructor;

import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;


@RestController
@RequestMapping("/api/goal-board")
@AllArgsConstructor
public class GoalBoardController {

    private final GoalBoardService goalBoardService;

    @GetMapping
    public ResponseEntity<GoalBoard> getGoalBoard(){
        return ResponseEntity.ok(goalBoardService.findByUserId());
    }

    @PostMapping
    public ResponseEntity<GoalBoard> addGoalToBoard(@RequestBody GoalDTO body) {
        return ResponseEntity.ok(goalBoardService.addGoalToBoard(body));
    }

    @GetMapping("/{goalId}")
    public ResponseEntity<Goal> getGoalById(@PathVariable Long goalId){
        return ResponseEntity.ok(goalBoardService.getGoalById(goalId));
    }
    
    @PutMapping("/{goalId}")
    public ResponseEntity<Goal> updateGoal(@PathVariable Long goalId, @RequestBody GoalDTO body){
        return ResponseEntity.ok(goalBoardService.updateGoal(goalId, body));
    }

    @DeleteMapping("/{goalId}")
    public ResponseEntity<?> deleteGoal(@PathVariable Long goalId){
        goalBoardService.deleteGoal(goalId);
        return ResponseEntity.ok().body(Map.of("message", "Meta excluída com sucesso"));
    }
}
