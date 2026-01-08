package com.example.graduation_project.events;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record EventsDto(
        @Null
        Long id,
        @NotBlank(message = "Name cannot be empty")
        String name,

        @Min(value  = 5, message ="The number of seats must be at least 10")
        int maxPlaces,

        @Future
        LocalDateTime date,

        @Min(value = 1, message = "The cost must be greater than 0")
        int cost,

        @Min(value = 30, message = "The duration must be more than 30 minutes")
        int duration,
        Long locationId
) {

}
