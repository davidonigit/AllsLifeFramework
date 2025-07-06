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
}
