package com.grupo3.allslife_framework.language.model;

import com.grupo3.allslife_framework.framework.model.AbstractRoutine;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("LANGUAGE")
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class LanguageRoutine extends AbstractRoutine {

    @Column(nullable = true)
    private String languageName;

	public String getLanguageName() {
		return languageName;
	}

	public void setSportName(String languageName) {
		this.languageName = languageName;
	}
    
    
}