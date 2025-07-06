package com.grupo3.allslife_framework.framework.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.grupo3.allslife_framework.framework.enums.DayOfWeekEnum;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
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
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "routine_id")
    @JsonBackReference
    private AbstractRoutine routine;

    public DailyAvailability(DayOfWeekEnum dayOfWeek, boolean morningAvailable, boolean afternoonAvailable, boolean eveningAvailable, AbstractRoutine routine) {
        this.dayOfWeek = dayOfWeek;
        this.morningAvailable = morningAvailable;
        this.afternoonAvailable = afternoonAvailable;
        this.eveningAvailable = eveningAvailable;
        this.routine = routine;
    }
}