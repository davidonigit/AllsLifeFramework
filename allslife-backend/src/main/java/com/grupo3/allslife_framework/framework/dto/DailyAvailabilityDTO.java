package com.grupo3.allslife_framework.framework.dto;

import com.grupo3.allslife_framework.framework.enums.DayOfWeekEnum;

public record DailyAvailabilityDTO(
    Long id,
     DayOfWeekEnum dayOfWeek,
      boolean morningAvailable,
       boolean afternoonAvailable,
        boolean eveningAvailable
    ) {}
