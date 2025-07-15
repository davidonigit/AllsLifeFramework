package com.grupo3.allslife_framework.studies.model;

import com.grupo3.allslife_framework.framework.model.AbstractRoutine;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("STUDY")
@Data
@EqualsAndHashCode(callSuper = true) // Importante para incluir os campos da superclasse no equals/hashCode
@AllArgsConstructor
@NoArgsConstructor
public class StudyRoutine extends AbstractRoutine {

    // Campo específico do contexto de estudos - área de conhecimento
    @Column(nullable = true)
    private String subjectArea;

    public String getSubjectArea() {
        return subjectArea;
    }

    public void setSubjectArea(String subjectArea) {
        this.subjectArea = subjectArea;
    }
}

