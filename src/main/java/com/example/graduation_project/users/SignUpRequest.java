package com.example.graduation_project.users;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SignUpRequest(
        @NotBlank
        @Size(min = 5)
        String login,
        @NotBlank
        @Size(min = 5)
        String password
) {
}
