package com.grupo10.bff_vitacare.dto;

import java.time.LocalDateTime;
import lombok.Data;

/**
 * Espejo de la entidad {@code ControlSalud} de {@code measurement-service},
 * devuelta por su historial de controles de salud.
 */
@Data
public class HealthControlDto {

    private Long idControl;

    private Long idPaciente;

    private LocalDateTime fechaHora;

    private String notas;

}
