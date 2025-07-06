package com.grupo3.allslife_framework.model;

import com.grupo3.allslife_framework.framework.model.AbstractRoutine;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("SPORT")
@Data
@EqualsAndHashCode(callSuper = true) // Importante para incluir os campos da superclasse no equals/hashCode
@NoArgsConstructor
public class SportRoutine extends AbstractRoutine {

    // Apenas o campo específico do contexto de esporte
    @Column(nullable = true)
    private String sportName;

    public SportRoutine(String sportName) {
        this.sportName = sportName;
    }

	public String getSportName() {
		return sportName;
	}

	public void setSportName(String sportName) {
		this.sportName = sportName;
	}
    
    
}