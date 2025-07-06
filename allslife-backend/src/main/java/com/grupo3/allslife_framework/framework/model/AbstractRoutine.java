package com.grupo3.allslife_framework.framework.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.grupo3.allslife_framework.framework.enums.DayOfWeekEnum;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.Data;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "ROUTINE_TYPE")
@Data
public abstract class AbstractRoutine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = true, columnDefinition = "TEXT")
    private String generatedRoutine;

    @OneToMany(mappedBy = "routine", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<DailyAvailability> weeklyAvailability = new ArrayList<>();

    @OneToOne(mappedBy = "routine")
    @JsonBackReference
    private User user;

    public DailyAvailability getAvailabilityForDay(DayOfWeekEnum day) {
        return weeklyAvailability.stream()
                .filter(a -> a.getDayOfWeek() == day)
                .findFirst()
                .orElse(null);
    }

    public void updateAvailability(DayOfWeekEnum day, boolean morning, boolean afternoon, boolean evening) {
        DailyAvailability availability = getAvailabilityForDay(day);
        if (availability != null) {
            availability.setMorningAvailable(morning);
            availability.setAfternoonAvailable(afternoon);
            availability.setEveningAvailable(evening);
        }
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getGeneratedRoutine() {
		return generatedRoutine;
	}

	public void setGeneratedRoutine(String generatedRoutine) {
		this.generatedRoutine = generatedRoutine;
	}

	public List<DailyAvailability> getWeeklyAvailability() {
		return weeklyAvailability;
	}

	public void setWeeklyAvailability(List<DailyAvailability> weeklyAvailability) {
		this.weeklyAvailability = weeklyAvailability;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
    
    
}