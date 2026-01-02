package com.example.graduation_project.users;

import com.example.graduation_project.security.jwt.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@Tag(name = "Users")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    private final AuthenticationService authenticationService;

    public UserController(
            UserService userService,
            AuthenticationService authenticationService
    ) {
        this.userService = userService;
        this.authenticationService = authenticationService;
    }

    @PostMapping
    @Operation(summary = "Регистрация нового пользователя")
    public ResponseEntity<UserDto> registerUser(
            @Valid SignUpRequest signUpRequest
    ) {
        log.info("Get request for sign up: login={}", signUpRequest.login());
        var user = userService.registerUser(signUpRequest);

        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(new UserDto(user.id(), user.login()));
    }

    @PostMapping("/auth")
    @Operation(summary = "Авторизация пользователя и получение JWT")
    public ResponseEntity<JwtTokenResponse> authenticate(
            @RequestBody @Valid SignInRequest signInRequest
    ) {
        log.info("Get request for sign in: login={}", signInRequest.login());
        var token = authenticationService.authenticateUser(signInRequest);

        return ResponseEntity.status(HttpStatus.OK)
                             .body(new JwtTokenResponse(token));
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Получить информацию о пользователе по ID")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<UserDto> getInfoAboutUser(
            @PathVariable("userId") Long id
    ) {
        log.info("Get request for user: id={}", id);
        var foungUser = userService.findByUserId(id);
        return ResponseEntity.ok(new UserDto(foungUser.id(), foungUser.login()));
    }

}
