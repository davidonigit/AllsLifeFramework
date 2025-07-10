package com.grupo3.allslife_framework.sports.dto;

import com.grupo3.allslife_framework.sports.enums.SportExperienceLevel;

public record SportUserPreferencesDTO(
    int age,
    SportExperienceLevel experienceLevel
){}
    

