package com.grupo10.bff_vitacare.dto;

import java.time.LocalDateTime;
import lombok.Data;

/**
 * Espejo de {@code MedicionVitalResponseDto} de {@code measurement-service}.
 */
@Data
public class VitalsDto {

    private Long idControl;

    private Long idPaciente;

    private LocalDateTime fechaHora;

    private String notas;

    private Integer presionSistolica;

    private Integer presionDiastolica;

    private double temperatura;

    private double peso;

}
