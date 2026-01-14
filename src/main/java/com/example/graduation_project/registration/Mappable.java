package com.example.graduation_project.registration;

public interface Mappable<E, D> {

    E toEntity(D domain);

    D toDomain(E entity);
}
