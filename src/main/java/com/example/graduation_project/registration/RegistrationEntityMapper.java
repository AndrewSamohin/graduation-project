package com.example.graduation_project.registration;

import com.example.graduation_project.events.Events;
import org.springframework.stereotype.Component;

@Component
public class RegistrationEntityMapper implements Mappable<RegistrationEntity, Registration>{
    @Override
    public RegistrationEntity toEntity(Registration registration) {
        RegistrationEntity entity = new RegistrationEntity();
        entity.setId(registration.id());

        return entity;
    }

    @Override
    public Registration toDomain(RegistrationEntity entity) {
        Events event = null;
        if (entity.getEvent() != null) {
            event = new Events(
                    entity.getEvent().getId(),
                    entity.getEvent().getName(),
                    entity.getEvent().getMaxPlaces(),
                    entity.getEvent().getDate(),
                    entity.getEvent().getCost(),
                    entity.getEvent().getDuration(),
                    entity.getEvent().getLocationId(),
                    entity.getEvent().getOwnerId()
            );
        }

        return new Registration(
                entity.getId(),
                event,
                entity.getUser() != null ? entity.getUser().getId() : null
        );
    }
}
