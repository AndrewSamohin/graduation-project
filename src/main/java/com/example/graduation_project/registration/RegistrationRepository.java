package com.example.graduation_project.registration;

import com.example.graduation_project.users.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RegistrationRepository extends JpaRepository<RegistrationEntity, Long> {
    Long countByEventId(Long eventId);

    boolean existsByUserIdAndEventId(Long userId, Long eventId);

    void deleteByUserIdAndEventId(Long userId, Long eventId);

    List<RegistrationEntity> user(UserEntity user);

    List<RegistrationEntity> findAllByUserId(Long userId);

    List<RegistrationEntity> findAllByEventId(Long eventId);
}
