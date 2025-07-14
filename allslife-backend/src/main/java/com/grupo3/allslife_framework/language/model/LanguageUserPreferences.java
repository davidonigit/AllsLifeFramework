package com.grupo3.allslife_framework.language.model;

import com.grupo3.allslife_framework.framework.model.AbstractUserPreferences;
import com.grupo3.allslife_framework.language.enums.LanguageExperienceLevel;
import com.grupo3.allslife_framework.language.enums.LanguageSkill;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("LANGUAGE")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class LanguageUserPreferences extends AbstractUserPreferences {

    @Enumerated(EnumType.STRING)
	@Column(nullable = true)
    private LanguageSkill languageSkill;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private LanguageExperienceLevel experienceLevel;

	public LanguageSkill getLanguageSkill() {
		return languageSkill;
	}

	public void setLanguageSkill(LanguageSkill languageSkill) {
		this.languageSkill = languageSkill;
	}

	public LanguageExperienceLevel getExperienceLevel() {
		return experienceLevel;
	}

	public void setExperienceLevel(LanguageExperienceLevel experienceLevel) {
		this.experienceLevel = experienceLevel;
	}

    
}

