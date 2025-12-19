package com.example.graduation_project.locations;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LocationService {

    private final LocationRepository locationRepository;

    private final LocationDtoConverter dtoConverter;

    private final LocationEntityConverter entityConverter;

    public LocationService(
            LocationRepository locationRepository,
            LocationDtoConverter dtoConverter,
            LocationEntityConverter entityConverter
    ) {
        this.locationRepository = locationRepository;
        this.dtoConverter = dtoConverter;
        this.entityConverter = entityConverter;
    }

    public List<Location> getAllLocations() {
        return locationRepository.findAll()
                .stream()
                .map(entityConverter::toDomain)
                .toList();
    }

    public Location getLocationById(Long id) {
        var foundLocation = locationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Not found location by id=%s".formatted(id)
                ));

        return entityConverter.toDomain(foundLocation);
    }

    public Location createLocation(Location locationToCreate) {
        var locationToSave = entityConverter.toEntity(locationToCreate);
        var savedEntity = locationRepository.save(locationToSave);

        return entityConverter.toDomain(savedEntity);
    }

    public Location updateLocation(Long id, Location locationToUpdate) {
        if(!locationRepository.existsById(id)){
            throw new EntityNotFoundException("No found location with id=%s".formatted(id));
        }
        var entityToUpdate = entityConverter.toEntity(locationToUpdate);
        entityToUpdate.setId(id);

        var updatedLocation = locationRepository.save(entityToUpdate);
        return entityConverter.toDomain(updatedLocation);
    }

    public void deleteLocation(Long id) {
        if(!locationRepository.existsById(id)){
            throw new EntityNotFoundException("No found location with id=%s".formatted(id));
        }
        locationRepository.deleteById(id);
    }

}
