package com.example.graduation_project.registration;

import com.example.graduation_project.events.EventsEntity;
import com.example.graduation_project.users.UserEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "registration")
public class RegistrationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private EventsEntity event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    public RegistrationEntity() {
    }

    public RegistrationEntity(
            Long id, EventsEntity event, UserEntity user) {
        this.id = id;
        this.event = event;
        this.user = user;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public EventsEntity getEvent() {
        return event;
    }

    public void setEvent(
            EventsEntity event
    ) {
        this.event = event;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
