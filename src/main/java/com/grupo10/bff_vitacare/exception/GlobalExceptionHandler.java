package com.grupo10.bff_vitacare.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import com.grupo10.bff_vitacare.dto.ErrorResponseDto;

import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Manejador global de excepciones para todos los controladores REST del BFF.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UpstreamUserNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleUpstreamUserNotFound(UpstreamUserNotFoundException ex) {
        ErrorResponseDto error = new ErrorResponseDto();
        error.setMessage(ex.getMessage());
        error.setStatus(HttpStatus.NOT_FOUND.value());
        error.setTimestamp(LocalDateTime.now());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(RegistrationConflictException.class)
    public ResponseEntity<ErrorResponseDto> handleRegistrationConflict(RegistrationConflictException ex) {
        ErrorResponseDto error = new ErrorResponseDto();
        error.setMessage(ex.getMessage());
        error.setStatus(HttpStatus.CONFLICT.value());
        error.setTimestamp(LocalDateTime.now());
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(PatientNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handlePatientNotFound(PatientNotFoundException ex) {
        ErrorResponseDto error = new ErrorResponseDto();
        error.setMessage(ex.getMessage());
        error.setStatus(HttpStatus.NOT_FOUND.value());
        error.setTimestamp(LocalDateTime.now());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UpstreamErrorException.class)
    public ResponseEntity<ErrorResponseDto> handleUpstreamError(UpstreamErrorException ex) {
        ErrorResponseDto error = new ErrorResponseDto();
        error.setMessage(ex.getMessage());
        error.setStatus(ex.getStatus().value());
        error.setTimestamp(LocalDateTime.now());
        return new ResponseEntity<>(error, ex.getStatus());
    }

}
