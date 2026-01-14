package com.example.graduation_project.registration;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record RegistrationDto(
        @Null
        Long id,

        @NotNull(message = "Event ID cannot be null")
        Long eventId,

        @NotNull(message = "User ID cannot be null")
        Long userId
) {
}
