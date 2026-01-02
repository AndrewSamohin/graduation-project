package com.example.graduation_project.users;

public record User(
        Long id,
        String login,
        String password,
        UserRole userRole
) {
}
