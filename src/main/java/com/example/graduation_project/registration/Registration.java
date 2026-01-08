package com.example.graduation_project.registration;

import com.example.graduation_project.events.Events;

public record Registration(
    Long id,
    Events event,
    Long userId
) {
}
