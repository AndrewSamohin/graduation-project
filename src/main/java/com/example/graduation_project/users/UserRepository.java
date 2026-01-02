package com.example.graduation_project.users;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    boolean existsByLogin(String login);

    Optional<UserEntity> findByLogin(String login);

}
