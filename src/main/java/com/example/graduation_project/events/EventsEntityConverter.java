package com.example.graduation_project.events;

import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class EventsEntityConverter {

    public EventsEntity toEntity(Events events) {
        EventsEntity entity = new EventsEntity();
        entity.setId(events.id());
        entity.setName(events.name());
        entity.setMaxPlaces(events.maxPlaces());
        entity.setDate(events.date());
        entity.setDuration(events.duration());
        entity.setCost(events.cost());
        entity.setLocationId(events.locationId());
        entity.setStatus(EventStatus.WAIT_START);
        entity.setRegistrations(new ArrayList<>());
        entity.setOwnerId(events.ownerId());
        return entity;
    }

    public Events toDomain(EventsEntity eventsEntity) {
        return new Events(
                eventsEntity.getId(),
                eventsEntity.getName(),
                eventsEntity.getMaxPlaces(),
                eventsEntity.getDate(),
                eventsEntity.getDuration(),
                eventsEntity.getCost(),
                eventsEntity.getLocationId(),
                eventsEntity.getOwnerId()
        );
    }

}
