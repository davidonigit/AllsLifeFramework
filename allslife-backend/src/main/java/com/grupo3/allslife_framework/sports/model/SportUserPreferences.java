package com.grupo3.allslife_framework.sports.model;

import com.grupo3.allslife_framework.framework.model.AbstractUserPreferences;
import com.grupo3.allslife_framework.sports.enums.SportExperienceLevel;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("SPORT") // O identificador para este tipo de preferência
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class SportUserPreferences extends AbstractUserPreferences {

    @Column(nullable = true)
    private int age;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private SportExperienceLevel experienceLevel;

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public SportExperienceLevel getExperienceLevel() {
		return experienceLevel;
	}

	public void setExperienceLevel(SportExperienceLevel experienceLevel) {
		this.experienceLevel = experienceLevel;
	}

    
}

