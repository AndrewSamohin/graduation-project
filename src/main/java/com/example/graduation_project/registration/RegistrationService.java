package com.example.graduation_project.registration;

import com.example.graduation_project.events.*;
import com.example.graduation_project.users.UserEntity;
import com.example.graduation_project.users.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RegistrationService {

    private static final Logger log = LoggerFactory.getLogger(RegistrationService.class);

    private final RegistrationRepository registrationRepository;

    private final UserRepository userRepository;

    private final EventRepository eventRepository;
    private final EventsEntityConverter eventsEntityConverter;
    private final EventsDtoConverter eventsDtoConverter;

    public RegistrationService(
            RegistrationRepository registrationRepository,
            UserRepository userRepository,
            EventRepository eventRepository,
            EventsEntityConverter eventsEntityConverter,
            EventsDtoConverter eventsDtoConverter
    ) {
        this.registrationRepository = registrationRepository;
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.eventsEntityConverter = eventsEntityConverter;
        this.eventsDtoConverter = eventsDtoConverter;
    }

    @Transactional
    public RegistrationEntity registrationForEvent(Long eventId, Long userId) {
        EventsEntity event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found"));

        if (event.getStatus() == EventStatus.CANCELLED
            || event.getStatus() == EventStatus.FINISHED) {
            throw new IllegalStateException("Registration is not possible: the event has been cancelled or has ended");
        }

        Long currentRegistrations = registrationRepository.countByEventId(eventId);
        if (currentRegistrations >= event.getMaxPlaces()) {
            throw new IllegalStateException("Registration is not possible: there are no available seats");
        }

        if (registrationRepository.existsByUserIdAndEventId(userId, eventId)) {
            throw new IllegalStateException("You are already registered for this event");
        }

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        RegistrationEntity registration = new RegistrationEntity(
                null,
                event,
                user
        );
        RegistrationEntity savedRegistration = registrationRepository.save(registration);
        log.info("User '{}' has registered for event '{}'.", user.getLogin(), eventId);
        return savedRegistration;
    }

    @Transactional
    public void cancellationOfRegistration(Long eventId, Long userId) {
        EventsEntity event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found"));

        if (event.getStatus() == EventStatus.STARTED
            || event.getStatus() == EventStatus.FINISHED) {
            throw new IllegalStateException("You cannot cancel your registration if the event has already started or ended.");
        }

        if (!registrationRepository.existsByUserIdAndEventId(userId, eventId)) {
            throw new IllegalStateException("You are not registered for the event");
        }

        registrationRepository.deleteByUserIdAndEventId(userId, eventId);
        log.info("User '{}' registration for event '{}' has been cancelled.", userId, eventId);
    }

    public List<EventsDto> getUserActivities(Long userId) {
        log.info("Getting activities for user '{}'.", userId);
        List<RegistrationEntity> registrationUser = registrationRepository.findAllByUserId(userId);

        List<EventsDto> eventsDtoList = registrationUser.stream()
                .map(RegistrationEntity::getEvent)
                .map(eventsEntityConverter::toDomain)
                .map(eventsDtoConverter::toDto)
                .collect(Collectors.toList());

        return eventsDtoList;
    }

}
