package com.grupo3.allslife_framework.dto;

import java.util.ArrayList;


public record SportRoutineDTO(
    String sport,
    ArrayList<DailyAvailabilityDTO> weeklyAvailability
) {}
