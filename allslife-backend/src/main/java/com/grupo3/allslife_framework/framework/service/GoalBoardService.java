package com.grupo3.allslife_framework.framework.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.grupo3.allslife_framework.framework.dto.GoalDTO;
import com.grupo3.allslife_framework.framework.exception.GoalBoardNotFoundException;
import com.grupo3.allslife_framework.framework.exception.GoalNotFoundException;
import com.grupo3.allslife_framework.framework.model.Goal;
import com.grupo3.allslife_framework.framework.model.GoalBoard;
import com.grupo3.allslife_framework.framework.repository.GoalBoardRepository;
import com.grupo3.allslife_framework.framework.repository.GoalRepository;
import com.grupo3.allslife_framework.framework.security.SecurityUtils;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class GoalBoardService {
    
    private final GoalBoardRepository goalBoardRepository;
    private final GoalRepository goalRepository;
    private final SecurityUtils securityUtils;

    public List<GoalBoard> getAllGoalBoards(){
        return goalBoardRepository.findAll();
    }

    public Optional<GoalBoard> getGoalBoardById(Long id){
        return goalBoardRepository.findById(id);
    }

    public GoalBoard findByUserId() {
        Long userId = securityUtils.getCurrentUserId();
        if (userId == null) {
            throw new GoalBoardNotFoundException("User ID not found in security context");
        }
        return goalBoardRepository.findByUserId(userId).orElseThrow(() -> new GoalBoardNotFoundException("GoalBoard not found with UserID: " + userId));
    }

    public Goal getGoalById(Long goalId){
        return goalRepository.findById(goalId).orElseThrow(() -> new GoalNotFoundException("Goal not found with ID: " + goalId));
    }

    public GoalBoard addGoalToBoard(GoalDTO dto){
        Goal goal = new Goal();
        goal.setName(dto.name());
        goal.setStatus(dto.status());

        GoalBoard goalBoard = findByUserId();
        
        goal.setGoalBoard(goalBoard);
        goalBoard.addGoal(goal);

        return saveGoalBoard(goalBoard);
    }

    public GoalBoard saveGoalBoard(GoalBoard goalBoard){
        return goalBoardRepository.save(goalBoard);
    }

    public Goal saveGoal(Goal goal){
        return goalRepository.save(goal);
    }
    
    @Transactional
    public void deleteGoal(Long goalId){
        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new GoalNotFoundException("Meta não encontrada com ID: " + goalId));
        
        GoalBoard goalBoard = goal.getGoalBoard();
        if(goalBoard != null){
            goalBoard.getGoals().remove(goal);
        }

        goalRepository.delete(goal);

    }

    @Transactional
    public Goal updateGoal(Long goalId, GoalDTO dto) {
        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new GoalNotFoundException("Meta não encontrada com ID: " + goalId));
        goal.setName(dto.name());
        goal.setStatus(dto.status());
        return saveGoal(goal);
    }
}
