package com.grupo3.allslife_framework.framework.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.grupo3.allslife_framework.framework.enums.StatusEnum;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class Goal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private StatusEnum status;

    @ManyToOne
    @JoinColumn(name = "goal_board_id", nullable = false)
    @JsonBackReference
    private GoalBoard goalBoard;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public StatusEnum getStatus() {
		return status;
	}

	public void setStatus(StatusEnum status) {
		this.status = status;
	}

	public GoalBoard getGoalBoard() {
		return goalBoard;
	}

	public void setGoalBoard(GoalBoard goalBoard) {
		this.goalBoard = goalBoard;
	}

    
}
