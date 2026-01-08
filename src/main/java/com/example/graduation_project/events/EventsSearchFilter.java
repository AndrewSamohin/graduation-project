package com.example.graduation_project.events;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record EventsSearchFilter(
        Long id,
        String name,
        Integer placesMin,
        Integer placesMax,
        LocalDateTime dateStartAfter,
        LocalDateTime dateStartBefore,
        BigDecimal costMin,
        BigDecimal costMax,
        Integer durationMin,
        Integer durationMax,
        Integer locationId,
        EventStatus eventStatus
) {
}
