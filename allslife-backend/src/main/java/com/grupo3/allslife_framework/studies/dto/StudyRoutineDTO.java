package com.grupo3.allslife_framework.studies.dto;

import java.util.ArrayList;

import com.grupo3.allslife_framework.framework.dto.DailyAvailabilityDTO;

public record StudyRoutineDTO(
    String subjectArea,
    ArrayList<DailyAvailabilityDTO> weeklyAvailability
) {}

