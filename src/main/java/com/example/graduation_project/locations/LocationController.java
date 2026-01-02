package com.example.graduation_project.locations;

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

@RestController
@Tag(name = "Locations")
public class LocationController {

    private static final Logger log = LoggerFactory.getLogger(LocationController.class);

    private final LocationService locService;

    private final LocationDtoConverter dtoConverter;

    public LocationController(
            LocationService locService,
            LocationDtoConverter dtoConverter
    ) {
        this.locService = locService;
        this.dtoConverter = dtoConverter;
    }

    @GetMapping("/locations")
    @Operation(summary = "Получить список всех локаций", description = "Allowed roles=[ADMIN, USER]")
    @SecurityRequirement(name = "bearerAuth")
    public List<LocationDto> getAllLocations() {
        log.info("Get request for getAllLocations");
        return locService.getAllLocations()
                .stream()
                .map(dtoConverter::toDto)
                .toList();
    }

    @PostMapping("/locations")
    @Operation(summary = "Создать новую локацию", description = "Allowed roles=[ADMIN]")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<LocationDto> createLocation(
            @Valid @RequestBody LocationDto locationDtoToCreate
    ) {
        log.info("Get request for create location: location={}", locationDtoToCreate);

        var createLocation = locService.createLocation(
                dtoConverter.toDomain(locationDtoToCreate)
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(dtoConverter.toDto(createLocation));
    }

    @DeleteMapping("/locations/{id}")
    @Operation(summary = "Удалить локацию по ID", description = "Allowed roles=[ADMIN]")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> deleteLocation(
            @PathVariable("id") Long id
    ) {
        log.info("Get request for delete location: id={}", id);
        locService.deleteLocation(id);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @GetMapping("/locations/{id}")
    @Operation(summary = "Получить локацию по ID", description = "Allowed roles=[ADMIN, USER]")
    @SecurityRequirement(name = "bearerAuth")
    public LocationDto getLocationById(
            @PathVariable("id") Long id
    ) {
        log.info("Get request for getLocationById: id={}", id);
        var foundLocation = locService.getLocationById(id);
        return dtoConverter.toDto(foundLocation);
    }

    @PutMapping("/locations/{id}")
    @Operation(summary = "Обновить локацию по ID", description = "Allowed roles=[ADMIN]")
    @SecurityRequirement(name = "bearerAuth")
    public LocationDto updateLocation(
        @PathVariable("id") Long id,
        @Valid @RequestBody LocationDto locationDtoToUpdate
    ) {
        log.info("Get request for update location: id={}, locationToUpdate={}",
                id, locationDtoToUpdate);

        var updatedLocation = locService.updateLocation(
                id,
                dtoConverter.toDomain(locationDtoToUpdate)
        );
        return dtoConverter.toDto(updatedLocation);
    }
}
