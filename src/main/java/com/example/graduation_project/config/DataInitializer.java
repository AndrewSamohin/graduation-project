package com.example.graduation_project.config;

import com.example.graduation_project.users.UserEntity;
import com.example.graduation_project.users.UserRepository;
import com.example.graduation_project.users.UserRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if(!userRepository.findByLogin("admin").isPresent()) {
            var user = new UserEntity(
                    null,
                    "admin",
                    passwordEncoder.encode("admin"),
                    UserRole.ADMIN.name()
            );
            userRepository.save(user);
            log.info("Created user '{}' with ADMIN role", user.getLogin());
        } else {
            log.info("User with ADMIN already exists");
        }

        if(!userRepository.findByLogin("user").isPresent()) {
            var user = new UserEntity(
                    null,
                    "user",
                    passwordEncoder.encode("user"),
                    UserRole.USER.name()
            );
            userRepository.save(user);
            log.info("Created user '{}' with USER role", user.getLogin());
        } else {
            log.info("User with USER already exists");
        }
    }
}
