package com.grupo3.allslife_app.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.grupo3.allslife_app.enums.DayOfWeekEnum;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SportRoutine {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = true)
    private String sportName;

    @Column(nullable = true, columnDefinition = "TEXT")
    private String generatedRoutine;
    
    @OneToMany(mappedBy = "sportRoutine", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<DailyAvailability> weeklyAvailability = new ArrayList<>();
    
    @OneToOne(mappedBy = "sportRoutine")
    @JsonBackReference
    private User user;

    {
        weeklyAvailability = new ArrayList<>();
    }

    public SportRoutine(String sportName) {
        if (sportName != null){
            this.sportName = sportName;
        }
    }

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
}
