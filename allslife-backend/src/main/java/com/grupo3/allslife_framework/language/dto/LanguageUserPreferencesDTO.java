package com.grupo3.allslife_framework.language.dto;

import com.grupo3.allslife_framework.language.enums.LanguageExperienceLevel;
import com.grupo3.allslife_framework.language.enums.LanguageSkill;

public record LanguageUserPreferencesDTO(
    LanguageSkill languageSkill,
    LanguageExperienceLevel experienceLevel
){}
    

