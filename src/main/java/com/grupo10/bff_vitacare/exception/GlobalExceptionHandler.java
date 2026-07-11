package com.grupo10.bff_vitacare.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import com.grupo10.bff_vitacare.dto.ErrorResponseDto;

import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Manejador global de excepciones para todos los controladores REST del BFF.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Traduce un usuario no encontrado en un microservicio ascendente a una
     * respuesta 404 con cuerpo {@link ErrorResponseDto}.
     *
     * @param ex excepción capturada
     * @return respuesta HTTP 404 con el detalle del error
     */
    @ExceptionHandler(UpstreamUserNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleUpstreamUserNotFound(UpstreamUserNotFoundException ex) {
        ErrorResponseDto error = new ErrorResponseDto();
        error.setMessage(ex.getMessage());
        error.setStatus(HttpStatus.NOT_FOUND.value());
        error.setTimestamp(LocalDateTime.now());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    /**
     * Traduce un conflicto de registro (ej. paciente ya registrado con ese RUT)
     * a una respuesta 409 con cuerpo {@link ErrorResponseDto}.
     *
     * @param ex excepción capturada
     * @return respuesta HTTP 409 con el detalle del error
     */
    @ExceptionHandler(RegistrationConflictException.class)
    public ResponseEntity<ErrorResponseDto> handleRegistrationConflict(RegistrationConflictException ex) {
        ErrorResponseDto error = new ErrorResponseDto();
        error.setMessage(ex.getMessage());
        error.setStatus(HttpStatus.CONFLICT.value());
        error.setTimestamp(LocalDateTime.now());
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    /**
     * Traduce un paciente no encontrado a una respuesta 404 con cuerpo
     * {@link ErrorResponseDto}.
     *
     * @param ex excepción capturada
     * @return respuesta HTTP 404 con el detalle del error
     */
    @ExceptionHandler(PatientNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handlePatientNotFound(PatientNotFoundException ex) {
        ErrorResponseDto error = new ErrorResponseDto();
        error.setMessage(ex.getMessage());
        error.setStatus(HttpStatus.NOT_FOUND.value());
        error.setTimestamp(LocalDateTime.now());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    /**
     * Traduce un error genérico proveniente de un microservicio ascendente,
     * preservando el código de estado HTTP que este haya devuelto.
     *
     * @param ex excepción capturada, con el status HTTP original del upstream
     * @return respuesta HTTP con el mismo status del error ascendente
     */
    @ExceptionHandler(UpstreamErrorException.class)
    public ResponseEntity<ErrorResponseDto> handleUpstreamError(UpstreamErrorException ex) {
        ErrorResponseDto error = new ErrorResponseDto();
        error.setMessage(ex.getMessage());
        error.setStatus(ex.getStatus().value());
        error.setTimestamp(LocalDateTime.now());
        return new ResponseEntity<>(error, ex.getStatus());
    }

    /**
     * Deja pasar el 404 nativo de Spring para rutas sin ningún handler
     * mapeado (ej. {@code GET /}), en vez de que el manejador genérico de
     * abajo lo convierta incorrectamente en un 500.
     *
     * @param ex excepción capturada
     * @return respuesta con {@link ErrorResponseDto} y estado 404 NOT FOUND
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleNoResourceFound(NoResourceFoundException ex) {
        ErrorResponseDto error = new ErrorResponseDto();
        error.setMessage("Recurso no encontrado");
        error.setStatus(HttpStatus.NOT_FOUND.value());
        error.setTimestamp(LocalDateTime.now());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    /**
     * Maneja cualquier excepción no anticipada explícitamente (incluidos
     * errores 5xx de los microservicios de dominio no traducidos por ningún
     * cliente, y problemas de red), devolviendo HTTP 500 con el mismo
     * contrato {@link ErrorResponseDto} en vez del cuerpo de error por
     * defecto de Spring Boot.
     *
     * @param ex excepción capturada
     * @return respuesta con {@link ErrorResponseDto} y estado 500 INTERNAL SERVER ERROR
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGenericException(Exception ex) {
        ErrorResponseDto error = new ErrorResponseDto();
        error.setMessage("Ocurrió un error inesperado al procesar la solicitud");
        error.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        error.setTimestamp(LocalDateTime.now());
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
