package com.grupo10.bff_vitacare.dto;

import java.time.LocalDateTime;
import lombok.Data;

/**
 * DTO de respuesta para errores de la API, en el mismo formato usado por
 * los demás microservicios de VitaCare.
 */
@Data
public class ErrorResponseDto {

    private String message;

    private int status;

    private LocalDateTime timestamp;

}
