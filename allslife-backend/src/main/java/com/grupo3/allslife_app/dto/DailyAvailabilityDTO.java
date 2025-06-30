package com.grupo3.allslife_app.dto;

import com.grupo3.allslife_app.enums.DayOfWeekEnum;

public record DailyAvailabilityDTO(
    Long id,
     DayOfWeekEnum dayOfWeek,
      boolean morningAvailable,
       boolean afternoonAvailable,
        boolean eveningAvailable
    ) {}
