package com.example.graduation_project.events;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@Tag(name = "Events")
public class EventController {

    private static final Logger log = LoggerFactory.getLogger(EventController.class);

    private final EventService eventService;

    private final EventsDtoConverter dtoConverter;

    public EventController(
            EventService eventService,
            EventsDtoConverter eventsDtoConverter
    ) {
        this.eventService = eventService;
        this.dtoConverter = eventsDtoConverter;
    }

    @PostMapping("/events")
    @Operation(summary = "Создание нового мероприятия")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<EventsDto> createEvent(
            @Valid @RequestBody EventsDto eventsDtoToCreate
    ) {
        log.info("Get request for create event: event={}", eventsDtoToCreate);

        var createEvent = eventService.createEvent(
                dtoConverter.toDomain(eventsDtoToCreate)
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(dtoConverter.toDto(createEvent));
    }

    @DeleteMapping("/events/{id}")
    @Operation(summary = "Удаление мероприятия")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> deleteEvent(
            @PathVariable("id") Long id
    ) {
        log.info("Get request for delete event: id={}", id);
        eventService.deleteEvent(id);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @GetMapping("/events/{id}")
    @Operation(summary = "Получение мероприятия по ID")
    @SecurityRequirement(name = "bearerAuth")
    public EventsDto getEventById(
            @PathVariable("id") Long id
    ) {
        log.info("Get request for getEventById: id={}", id);
        var foundEvent = eventService.getEventById(id);
        return dtoConverter.toDto(foundEvent);
    }

    @PutMapping("/events/{id}")
    @Operation(summary = "Обновление мероприятия")
    @SecurityRequirement(name = "bearerAuth")
    public EventsDto updateEvent(
            @PathVariable("id") Long id,
            @Valid @RequestBody EventsDto eventsDtoToUpdate
    ) {
        log.info("Get request for update event: id={}, eventToUpdate={}",
                id, eventsDtoToUpdate);

        var updatedEvent = eventService.updateEvent(
                id,
                dtoConverter.toDomain(eventsDtoToUpdate)
        );
        return dtoConverter.toDto(updatedEvent);
    }

    @PostMapping("/events/search")
    @Operation(summary = "Поиск мероприятий по фильтру")
    @SecurityRequirement(name = "bearerAuth")
    public List<EventsDto> searchEvents(
            @RequestBody EventsSearchFilter eventsSearchFilter
    ) {
        log.info("Get request for search events: events={}", eventsSearchFilter);
        List<Events> eventsList = eventService.searchAllEvents(eventsSearchFilter);

        return eventsList.stream()
                         .map(dtoConverter::toDto)
                         .collect(Collectors.toList());
    }

    @GetMapping("/events/my")
    @Operation(summary = "Получение всех мероприятий, созданных текущим пользователем")
    @SecurityRequirement(name = "bearerAuth")
    public List<EventsDto> getMyEvents() {
        List<Events> events = eventService.getUserEvents();

        return events.stream()
                .map(dtoConverter::toDto)
                .toList();
    }

}
