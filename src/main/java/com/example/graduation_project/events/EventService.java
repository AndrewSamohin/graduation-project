package com.example.graduation_project.events;

import com.example.graduation_project.locations.LocationEntity;
import com.example.graduation_project.locations.LocationRepository;
import com.example.graduation_project.users.User;
import com.example.graduation_project.users.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EventService {

    private final EventRepository eventRepository;

    private final EventsEntityConverter entityConverter;

    private final LocationRepository locationRepository;

    private final UserService userService;

    public EventService(
            EventRepository eventRepository,
            EventsEntityConverter entityConverter,
            LocationRepository locationRepository,
            UserService userService
    ) {
        this.eventRepository = eventRepository;
        this.entityConverter = entityConverter;
        this.locationRepository = locationRepository;
        this.userService = userService;
    }

    public Events createEvent(Events eventsToCreate) {
        LocationEntity location = locationRepository
                .findById(eventsToCreate.locationId())
                .orElseThrow(() ->
                                new EntityNotFoundException("Location not found")
                );

        if (location.getCapacity() < eventsToCreate.maxPlaces()) {
            throw new IllegalStateException(
                    "The capacity of the location is less than the number of seats for the event"
            );
        }

        Long ownerId = getCurrentUserId();

        EventsEntity eventWithStatus = new EventsEntity(
                null,
                eventsToCreate.name(),
                eventsToCreate.maxPlaces(),
                eventsToCreate.date(),
                eventsToCreate.duration(),
                eventsToCreate.cost(),
                eventsToCreate.locationId(),
                EventStatus.WAIT_START,
                List.of(),
                ownerId
        );

        var savedEntity = eventRepository.save(eventWithStatus);

        return entityConverter.toDomain(savedEntity);
    }

    @Transactional
    public void deleteEvent(Long eventId) {
        EventsEntity event = eventRepository
                .findById(eventId)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "No found event with id=%s".formatted(eventId))
                );

        if (event.getStatus() == EventStatus.FINISHED
            || event.getStatus() == EventStatus.STARTED) {
            throw new IllegalStateException(
                    "Only events that have not started can be cancelled"
            );
        }

        Long currentUserId = getCurrentUserId();
        boolean isAdmin = isAdmin();

        if(!isAdmin && !event.getOwnerId().equals(currentUserId)){
            throw new AuthorizationDeniedException(
                    "You do not have permission to perform this operation"
            );
        }

        event.setStatus(EventStatus.CANCELLED);
        eventRepository.save(event);
    }

    public Events getEventById(Long id) {
        var foundEvent = eventRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Not found event by id=%s".formatted(id)
                ));
        return entityConverter.toDomain(foundEvent);
    }

    public Events updateEvent(Long id, Events eventsToUpdate) {
        if (!eventRepository.existsById(id)) {
            throw new EntityNotFoundException("No found event by id=%s".formatted(id));
        }
        var entityToUpdate = entityConverter.toEntity(eventsToUpdate);
        entityToUpdate.setId(id);

        var updatedEvent = eventRepository.save(entityToUpdate);
        return entityConverter.toDomain(updatedEvent);
    }

    public List<Events> searchAllEvents(EventsSearchFilter eventsSearchFilter) {
        List<EventsEntity> entityList = eventRepository.findEvents(
                eventsSearchFilter.name(),
                eventsSearchFilter.placesMin(),
                eventsSearchFilter.placesMax(),
                eventsSearchFilter.dateStartAfter(),
                eventsSearchFilter.dateStartBefore(),
                eventsSearchFilter.costMin(),
                eventsSearchFilter.costMax(),
                eventsSearchFilter.durationMin(),
                eventsSearchFilter.durationMax(),
                eventsSearchFilter.locationId() != null ?
                        eventsSearchFilter.locationId() : null,
                eventsSearchFilter.eventStatus()
        );

        return entityList.stream()
                .map(entityConverter::toDomain)
                .toList();
    }

    public List<Events> getUserEvents() {
        Long currentUserId = getCurrentUserId();
        List<EventsEntity> entities = eventRepository.findByOwnerId(currentUserId);

        return entities.stream()
                       .map(entityConverter::toDomain)
                       .toList();
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        return currentUser.id();
    }

    private boolean isAdmin() {
        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();
        return authentication.getAuthorities()
                             .stream()
                             .map(GrantedAuthority::getAuthority)
                             .anyMatch(role -> role.equals("ADMIN"));
    }

}
