package com.grupo3.allslife_framework.studies.dto;

import com.grupo3.allslife_framework.studies.enums.StudyMaterial;

public record StudyUserPreferencesDTO(
    StudyMaterial materialPreferred,
    Integer maxTime
){}

