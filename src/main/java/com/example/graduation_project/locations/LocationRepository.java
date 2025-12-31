package com.example.graduation_project.locations;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocationRepository
        extends JpaRepository<LocationEntity, Long> {
}