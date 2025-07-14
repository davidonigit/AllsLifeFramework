package com.grupo3.allslife_framework.language.dto;

import java.util.ArrayList;

import com.grupo3.allslife_framework.framework.dto.DailyAvailabilityDTO;


public record LanguageRoutineDTO(
    String language,
    ArrayList<DailyAvailabilityDTO> weeklyAvailability
) {}
