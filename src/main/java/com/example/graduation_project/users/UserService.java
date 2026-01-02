package com.example.graduation_project.users;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerUser(SignUpRequest signUpRequest) {
        if (userRepository.existsByLogin(signUpRequest.login())) {
            throw new IllegalArgumentException("Username already exists");
        }
        var userToSave = new UserEntity(
                null,
                signUpRequest.login(),
                passwordEncoder.encode(signUpRequest.password()),
                UserRole.USER.name()
        );
        var saved = userRepository.save(userToSave);
        return mapToDomain(saved);
    }

    public User findByUserId(Long id) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        return mapToDomain(user);
    }

    public User findByLogin(String login) {
        var user = userRepository.findByLogin(login)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        return mapToDomain(user);
    }

    private static User mapToDomain(UserEntity entity) {
        return new User(
                entity.getId(),
                entity.getLogin(),
                entity.getPasswordHash(),
                UserRole.valueOf(entity.getRole())
        );
    }
}
