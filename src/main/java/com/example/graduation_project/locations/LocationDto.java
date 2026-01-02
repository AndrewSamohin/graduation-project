package com.example.graduation_project.locations;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record LocationDto(
        //Видел корректировку на GitHub, но если ставлю NotNull,
        //у меня возвращается ответ 200 с сообщением, что id не должен быть null.
        //Вернул как было
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
