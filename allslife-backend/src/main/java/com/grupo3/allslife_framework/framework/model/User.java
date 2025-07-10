package com.grupo3.allslife_framework.framework.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "goal_board_id", unique = true)
    @JsonManagedReference
    private GoalBoard goalBoard;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "routine_id", unique = true)
    @JsonManagedReference
    private AbstractRoutine routine;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    private AbstractUserPreferences preferences;

    @OneToMany(cascade = CascadeType.ALL)
    @JsonBackReference
    private List<RoutineHistory> routineHistory;

    @OneToMany(cascade = CascadeType.ALL)
    @JsonBackReference
    private List<Notification> notifications;
    
	public User(Long id, String name, String email, String password, GoalBoard goalBoard, AbstractRoutine routine,
			AbstractUserPreferences preferences, List<RoutineHistory> routineHistory,
			List<Notification> notifications) {
		super();
		this.id = id;
		this.name = name;
		this.email = email;
		this.password = password;
		this.goalBoard = goalBoard;
		this.routine = routine;
		this.preferences = preferences;
		this.routineHistory = routineHistory;
		this.notifications = notifications;
	}

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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public GoalBoard getGoalBoard() {
		return goalBoard;
	}

	public void setGoalBoard(GoalBoard goalBoard) {
		this.goalBoard = goalBoard;
	}

	public AbstractRoutine getRoutine() {
		return routine;
	}

	public void setRoutine(AbstractRoutine routine) {
		this.routine = routine;
	}

	public AbstractUserPreferences getPreferences() {
		return preferences;
	}

	public void setPreferences(AbstractUserPreferences preferences) {
		this.preferences = preferences;
	}

	public List<RoutineHistory> getRoutineHistory() {
		return routineHistory;
	}

	public void setRoutineHistory(List<RoutineHistory> routineHistory) {
		this.routineHistory = routineHistory;
	}

	public List<Notification> getNotifications() {
		return notifications;
	}

	public void setNotifications(List<Notification> notifications) {
		this.notifications = notifications;
	}
    
    
}
