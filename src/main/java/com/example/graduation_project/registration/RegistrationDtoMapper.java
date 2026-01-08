package com.example.graduation_project.registration;

import org.springframework.stereotype.Component;

@Component
public class RegistrationDtoMapper implements Mappable<RegistrationDto, Registration>{
    @Override
    public RegistrationDto toEntity(Registration registration) {
        return new RegistrationDto(
                registration.id(),
                registration.event() != null ? registration.event().id() : null,
                registration.userId()
        );
    }

    @Override
    public Registration toDomain(RegistrationDto registrationDto) {
        return new Registration(
                registrationDto.id(),
                null,
                registrationDto.userId()
        );
    }
}
