package com.example.graduation_project.registration;

import com.example.graduation_project.events.EventsDto;
import com.example.graduation_project.users.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/events/registrations")
@Tag(name = "Events")
public class RegistrationController {

    private static final Logger log = LoggerFactory.getLogger(RegistrationController.class);

    private final RegistrationService registrationService;

    private final RegistrationDtoMapper registrationDtoMapper;

    private final RegistrationEntityMapper registrationEntityMapper;

    public RegistrationController(
            RegistrationService registrationService,
            RegistrationDtoMapper registrationDtoMapper,
            RegistrationEntityMapper registrationEntityMapper
    ) {
        this.registrationService = registrationService;
        this.registrationDtoMapper = registrationDtoMapper;
        this.registrationEntityMapper = registrationEntityMapper;
    }

    @PostMapping("/{eventId}")
    @Operation(summary = "Регистрация пользователя на мероприятие по ID")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<RegistrationDto> registrationForEvent(
            @PathVariable("eventId") Long eventId
    ) {
        log.info("Registration for event {}", eventId);

        Authentication authentication = SecurityContextHolder
                .getContext().getAuthentication();
        Long userId = ((User) authentication.getPrincipal()).id();

        RegistrationEntity savedRegistration = registrationService
                .registrationForEvent(eventId, userId);

        RegistrationDto registrationDto = registrationDtoMapper
                .toEntity(registrationEntityMapper.toDomain(savedRegistration));
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(registrationDto);
    }

    @DeleteMapping("/cancel/{eventId}")
    @Operation(summary = "Отмена регистрации на мероприятие")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> cancellationOfRegistrationForEvent(
            @PathVariable("eventId") Long eventId
    ) {
        log.info("Cancellation of registration for event {}", eventId);
        Authentication authentication = SecurityContextHolder
                .getContext().getAuthentication();
        Long userId = ((User) authentication.getPrincipal()).id();

        registrationService.cancellationOfRegistration(eventId, userId);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @GetMapping("/my")
    @Operation(summary = "Получение мероприятий, на которые зарегистрирован текущий пользователь")
    @SecurityRequirement(name = "bearerAuth")
    public List<EventsDto> getActivities() {
        Authentication authentication = SecurityContextHolder.getContext()
                                                             .getAuthentication();
        Long userId = ((User) authentication.getPrincipal()).id();

        List<EventsDto> userEvents = registrationService.getUserActivities(userId);
        return userEvents;
    }

}
