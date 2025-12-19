package com.example.graduation_project.locations;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record LocationDto(

        @Null
        Long id,

        @Size(max = 30)
        @NotBlank(message = "Name cannot be empty")
        String name,

        @Size(max = 50)
        @NotBlank(message = "Address cannot be empty")
        String address,

        @Min(value = 5, message = "Capacity must be greater than 4")
        int capacity,
        String description
) {
}
