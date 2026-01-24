package com.example.graduation_project.events;

import com.example.graduation_project.kafka.EventChangeMessage;
import com.example.graduation_project.kafka.FieldChange;
import com.example.graduation_project.locations.LocationEntity;
import com.example.graduation_project.locations.LocationRepository;
import com.example.graduation_project.locations.LocationService;
import com.example.graduation_project.registration.RegistrationRepository;
import com.example.graduation_project.users.User;
import com.example.graduation_project.users.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Consumer;

@Service
public class EventService {

    private static final Logger log = LoggerFactory.getLogger(EventService.class);

    private final EventRepository eventRepository;

    private final EventsEntityConverter entityConverter;

    private final LocationRepository locationRepository;

    private final LocationService locationService;

    private final RegistrationRepository registrationRepository;
    private final KafkaTemplate<String, EventChangeMessage> kafkaTemplate;

    @Value("${spring.kafka.template.default-topic}")
    private String eventChangeTopic;

    public EventService(
            EventRepository eventRepository,
            EventsEntityConverter entityConverter,
            LocationRepository locationRepository,
            LocationService locationService,
            RegistrationRepository registrationRepository,
            KafkaTemplate<String, EventChangeMessage> kafkaTemplate
    ) {
        this.eventRepository = eventRepository;
        this.entityConverter = entityConverter;
        this.locationRepository = locationRepository;
        this.locationService = locationService;
        this.registrationRepository = registrationRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    public Events createEvent(Events eventsToCreate) {
        log.info("Creating an event");

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
        log.info("The event has been created and saved");

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
            log.info("Exception: unsuccessful cancellation");
            throw new IllegalStateException(
                    "Only events that have not started can be cancelled"
            );
        }

        Long currentUserId = getCurrentUserId();
        boolean isAdmin = isAdmin();

        if(!isAdmin && !event.getOwnerId().equals(currentUserId)){
            log.info("Exception: Insufficient rights");
            throw new AuthorizationDeniedException(
                    "You do not have permission to perform this operation"
            );
        }
        log.info("Event successfully deleted");

        event.setStatus(EventStatus.CANCELLED);
        eventRepository.save(event);
    }

    public Events getEventById(Long id) {
        var foundEvent = eventRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Not found event by id=%s".formatted(id)
                ));
        log.info("Found event by id={}", foundEvent);

        return entityConverter.toDomain(foundEvent);
    }

    @Transactional
    public Events updateEvent(Long id, Events eventsToUpdate) {
        EventsEntity entity = eventRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No found event by id=" + id));

        Long currentUserId = getCurrentUserId();
        boolean isAdmin = isAdmin();
        if (!isAdmin && !entity.getOwnerId().equals(currentUserId)) {
            throw new AuthorizationDeniedException("You do not have permission to update this event");
        }

        Map<String, FieldChange> changedFields =
                applyUpdatesChanges(entity, eventsToUpdate);

        eventRepository.save(entity);
        log.info("Updated event with id={}", entity.getId());

        if (!changedFields.isEmpty()) {
            List<Long> subscriberIds = registrationRepository.findAllByEventId(id)
                    .stream()
                    .map(sub -> sub.getUser().getId())
                    .toList();

            EventChangeMessage message = new EventChangeMessage(
                    id,
                    currentUserId,
                    entity.getOwnerId(),
                    changedFields,
                    subscriberIds
            );
            log.info("Sending Kafka message to topic '{}': {}", eventChangeTopic, message);

            try {
                kafkaTemplate.send(eventChangeTopic, message);
                log.info("Sent event change message to Kafka: eventId={}, changerId={}", id, currentUserId);
            } catch (KafkaException e) {
                log.error("Failed to send Kafka message for eventId={}", id, e);
            }
        } else {
            log.info("No fields changed for eventId={} skipping Kafka send", id);
        }

        return entityConverter.toDomain(entity);
    }

    private Map<String, FieldChange> applyUpdatesChanges(
            EventsEntity entity,
            Events dto
    ) {
        Map<String, FieldChange> changes = new HashMap<>();

        update(changes, "name",
                entity.getName(), dto.name(), entity::setName);

        update(changes, "maxPlaces",
                entity.getMaxPlaces(), dto.maxPlaces(), entity::setMaxPlaces);

        update(changes, "date",
                entity.getDate(), dto.date(), entity::setDate);

        update(changes, "cost",
                entity.getCost(), dto.cost(), entity::setCost);

        update(changes, "duration",
                entity.getDuration(), dto.duration(), entity::setDuration);

        update(changes, "locationId",
                entity.getLocationId(), dto.locationId(), entity::setLocationId);

        return changes;
    }

    private <T> void update(
            Map<String, FieldChange> changes,
            String field,
            T oldValue,
            T newValue,
            Consumer<T> setter
    ) {
        if(!Objects.equals(oldValue, newValue)){
            changes.put(field, new FieldChange(oldValue, newValue));
            setter.accept(newValue);
        }
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
        log.info("Found {} events", entityList.size());

        return entityList.stream()
                .map(entityConverter::toDomain)
                .toList();
    }

    public List<Events> getUserEvents() {
        Long currentUserId = getCurrentUserId();
        List<EventsEntity> entities = eventRepository.findByOwnerId(currentUserId);
        log.info("Found events user. UserId={}", currentUserId);

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
