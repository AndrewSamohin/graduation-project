package com.example.graduation_project.security.jwt;

import com.example.graduation_project.users.SignInRequest;
import com.example.graduation_project.users.User;
import com.example.graduation_project.users.UserEntity;
import com.example.graduation_project.users.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;

    private final UserRepository userRepository;

    private final JwtTokenManager jwtTokenManager;

    public AuthenticationService(
            AuthenticationManager authenticationManager,
            UserRepository userRepository,
            JwtTokenManager jwtTokenManager
    ) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.jwtTokenManager = jwtTokenManager;
    }

    public String authenticateUser(SignInRequest signInRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        signInRequest.login(),
                        signInRequest.password()
                )
        );

        UserEntity user = userRepository.findByLogin(signInRequest.login())
                    .orElseThrow(() -> new RuntimeException("User not found"));

        return jwtTokenManager.generateToken(user.getLogin(), user.getRole(), user.getId());
    }
}
