package com.grupo3.allslife_framework.dto;

import java.util.ArrayList;

import com.grupo3.allslife_framework.framework.dto.DailyAvailabilityDTO;


public record SportRoutineDTO(
    String sport,
    ArrayList<DailyAvailabilityDTO> weeklyAvailability
) {}
