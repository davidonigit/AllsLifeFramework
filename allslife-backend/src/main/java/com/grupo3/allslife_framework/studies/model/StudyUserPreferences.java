package com.grupo3.allslife_framework.studies.model;

import com.grupo3.allslife_framework.framework.model.AbstractUserPreferences;
import com.grupo3.allslife_framework.studies.enums.StudyMaterial;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("STUDY") // O identificador para este tipo de preferência
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class StudyUserPreferences extends AbstractUserPreferences {

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private StudyMaterial materialPreferred;

    @Column(nullable = true)
    private Integer maxTime;

    public StudyMaterial getMaterialPreferred() {
        return materialPreferred;
    }

    public void setMaterialPreferred(StudyMaterial materialPreferred) {
        this.materialPreferred = materialPreferred;
    }

    public Integer getMaxTime() {
        return maxTime;
    }

    public void setMaxTime(Integer maxTime) {
        this.maxTime = maxTime;
    }
}

