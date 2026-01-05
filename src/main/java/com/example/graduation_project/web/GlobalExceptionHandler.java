package com.example.graduation_project.web;

import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgument(Exception exception) {
        log.warn("Handle bad request exception", exception);
        ErrorMessageResponse messageResponse = new ErrorMessageResponse(
                "Request validation error",
                exception.getMessage(),
                LocalDateTime.now()
        );

        return ResponseEntity
                .status(400)
                .body(messageResponse);
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorMessageResponse> handleGenericException(Exception exception) {
        log.error("Handle generic exception", exception);
        ErrorMessageResponse messageResponse = new ErrorMessageResponse(
                "Server error",
                exception.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(messageResponse);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorMessageResponse> handleNotFoundException(
            EntityNotFoundException exception
    ) {
        log.error("Handle found exception", exception);
        ErrorMessageResponse messageResponse = new ErrorMessageResponse(
                "Entity not found",
                exception.getMessage(),
                LocalDateTime.now()
        );
        return  ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(messageResponse);
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    protected ResponseEntity<ErrorMessageResponse> handleAuthorizationException(
            AuthorizationDeniedException exception
        ) {
        log.error("Handle authorization exception", exception);
        ErrorMessageResponse messageResponse = new ErrorMessageResponse(
                "Access denied",
                exception.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(messageResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    private static ResponseEntity<ErrorMessageResponse> constructMethodArgumentNotValidMessage(
            MethodArgumentNotValidException e
    ) {
        ErrorMessageResponse messageResponse = new ErrorMessageResponse(
                "Empty field",
                e.getMessage(),
                LocalDateTime.now()
        );

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(messageResponse);
    }
}
