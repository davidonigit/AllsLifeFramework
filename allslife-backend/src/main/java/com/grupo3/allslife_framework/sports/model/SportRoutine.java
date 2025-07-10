package com.grupo3.allslife_framework.sports.model;

import com.grupo3.allslife_framework.framework.model.AbstractRoutine;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("SPORT")
@Data
@EqualsAndHashCode(callSuper = true) // Importante para incluir os campos da superclasse no equals/hashCode
@AllArgsConstructor
@NoArgsConstructor
public class SportRoutine extends AbstractRoutine {

    // Apenas o campo específico do contexto de esporte
    @Column(nullable = true)
    private String sportName;

	public String getSportName() {
		return sportName;
	}

	public void setSportName(String sportName) {
		this.sportName = sportName;
	}
    
    
}