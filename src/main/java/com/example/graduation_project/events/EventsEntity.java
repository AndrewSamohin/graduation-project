package com.example.graduation_project.events;

import com.example.graduation_project.registration.RegistrationEntity;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "events")
public class EventsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name_event", nullable = false)
    String name;

    @Column(name = "max_places", nullable = false)
    int maxPlaces;

    @Column(name = "date", nullable = false)
    LocalDateTime date;

    @Column(name = "duration", nullable = false)
    int duration;

    @Column(name = "cost", nullable = false)
    int cost;

    @Column(name = "location_id", nullable = false)
    Long locationId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    EventStatus status;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RegistrationEntity> registrations = new ArrayList<>();

    @Column(name = "owner_id", nullable = false)
    private Long ownerId;

    public EventsEntity() {
    }

    public EventsEntity(
            Long id,
            String name,
            int maxPlaces,
            LocalDateTime date,
            int duration,
            int cost,
            Long locationId,
            EventStatus status,
            List<RegistrationEntity> registrations,
            Long ownerId
    ) {
        this.id = id;
        this.name = name;
        this.maxPlaces = maxPlaces;
        this.date = date;
        this.duration = duration;
        this.cost = cost;
        this.locationId = locationId;
        this.status = status;
        this.registrations = registrations;
        this.ownerId = ownerId;
    }

    public List<RegistrationEntity> getRegistrations() {
        return registrations;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public void setRegistrations(
            List<RegistrationEntity> registrations
    ) {
        this.registrations = registrations;
    }

    public EventStatus getStatus() {
        return status;
    }

    public void setStatus(
            EventStatus status
    ) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMaxPlaces() {
        return maxPlaces;
    }

    public void setMaxPlaces(int maxPlaces) {
        this.maxPlaces = maxPlaces;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public Long getLocationId() {
        return locationId;
    }

    public void setLocationId(Long locationId) {
        this.locationId = locationId;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
