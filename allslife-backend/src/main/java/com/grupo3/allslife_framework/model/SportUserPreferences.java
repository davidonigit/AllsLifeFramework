package com.grupo3.allslife_framework.model;

import com.grupo3.allslife_framework.enums.SportExperienceLevel;
import com.grupo3.allslife_framework.framework.model.AbstractUserPreferences;

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

}

