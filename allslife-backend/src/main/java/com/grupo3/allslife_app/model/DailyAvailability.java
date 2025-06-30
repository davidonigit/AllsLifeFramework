package com.grupo3.allslife_app.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.grupo3.allslife_app.enums.DayOfWeekEnum;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DailyAvailability {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    private DayOfWeekEnum dayOfWeek;
    
    @Column(nullable = false)
    private boolean morningAvailable;
    
    @Column(nullable = false)
    private boolean afternoonAvailable;
    
    @Column(nullable = false)
    private boolean eveningAvailable;
    
    @ManyToOne
    @JoinColumn(name = "sport_routine_id")
    @JsonBackReference
    private SportRoutine sportRoutine;
}