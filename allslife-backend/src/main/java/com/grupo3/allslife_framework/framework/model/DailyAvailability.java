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

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public DayOfWeekEnum getDayOfWeek() {
		return dayOfWeek;
	}

	public void setDayOfWeek(DayOfWeekEnum dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
	}

	public boolean isMorningAvailable() {
		return morningAvailable;
	}

	public void setMorningAvailable(boolean morningAvailable) {
		this.morningAvailable = morningAvailable;
	}

	public boolean isAfternoonAvailable() {
		return afternoonAvailable;
	}

	public void setAfternoonAvailable(boolean afternoonAvailable) {
		this.afternoonAvailable = afternoonAvailable;
	}

	public boolean isEveningAvailable() {
		return eveningAvailable;
	}

	public void setEveningAvailable(boolean eveningAvailable) {
		this.eveningAvailable = eveningAvailable;
	}

	public AbstractRoutine getRoutine() {
		return routine;
	}

	public void setRoutine(AbstractRoutine routine) {
		this.routine = routine;
	}
    
    
}