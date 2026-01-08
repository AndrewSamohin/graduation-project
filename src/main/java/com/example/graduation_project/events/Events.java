package com.example.graduation_project.events;

import java.time.LocalDateTime;

public record Events(
        Long id,
        String name,
        int maxPlaces,
        LocalDateTime date,
        int cost,
        int duration,
        Long locationId,
        Long ownerId
) {
}
