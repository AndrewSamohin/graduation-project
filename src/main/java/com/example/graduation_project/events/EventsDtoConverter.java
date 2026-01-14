package com.example.graduation_project.events;

import org.springframework.stereotype.Component;

@Component
public class EventsDtoConverter {

    public EventsDto toDto(Events events) {
        return new EventsDto(
                events.id(),
                events.name(),
                events.maxPlaces(),
                events.date(),
                events.cost(),
                events.duration(),
                events.locationId()
        );
    }

    public Events toDomain(EventsDto eventsDto) {
        return new Events(
                eventsDto.id(),
                eventsDto.name(),
                eventsDto.maxPlaces(),
                eventsDto.date(),
                eventsDto.cost(),
                eventsDto.duration(),
                eventsDto.locationId(),
                null
        );
    }

}
