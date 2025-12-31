package com.example.graduation_project.locations;

public record Location(
        Long id,
        String name,
        String address,
        int capacity,
        String description
) {
}
