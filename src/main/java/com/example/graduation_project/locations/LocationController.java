package com.example.graduation_project.locations;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class LocationController {

    private static final Logger log = LoggerFactory.getLogger(LocationController.class);

    private final LocationService locationService;

    private final LocationDtoConverter dtoConverter;

    public LocationController(
            LocationService locationService,
            LocationDtoConverter dtoConverter
    ) {
        this.locationService = locationService;
        this.dtoConverter = dtoConverter;
    }

    @GetMapping("/locations")
    public List<LocationDto> getAllLocations() {
        log.info("Get request for getAllLocations");
        return locationService.getAllLocations()
                .stream()
                .map(dtoConverter::toDto)
                .toList();
    }

    @PostMapping("/locations")
    public ResponseEntity<LocationDto> createLocation(
            @Valid @RequestBody LocationDto locationDtoToCreate
    ) {
        log.info("Get request for create location: location={}", locationDtoToCreate);

        var createLocation = locationService.createLocation(
                dtoConverter.toDomain(locationDtoToCreate)
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(dtoConverter.toDto(createLocation));
    }

    @DeleteMapping("/locations/{id}")
    public ResponseEntity<Void> deleteLocation(
            @PathVariable("id") Long id
    ) {
        log.info("Get request for delete location: id={}", id);
        locationService.deleteLocation(id);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @GetMapping("/locaitons/{id}")
    public LocationDto getLocationById(
            @PathVariable("id") Long id
    ) {
        log.info("Get request for getLocationById: id={}", id);
        var foundLocation = locationService.getLocationById(id);
        return dtoConverter.toDto(foundLocation);
    }

    @PutMapping("/locations/{id}")
    public LocationDto updateLocation(
        @PathVariable("id") Long id,
        @Valid @RequestBody LocationDto locationDtoToUpdate
    ) {
        log.info("Get request for update location: id={}, locationToUpdate={}",
                id, locationDtoToUpdate);

        var updatedLocation = locationService.updateLocation(
                id,
                dtoConverter.toDomain(locationDtoToUpdate)
        );
        return dtoConverter.toDto(updatedLocation);
    }
}
