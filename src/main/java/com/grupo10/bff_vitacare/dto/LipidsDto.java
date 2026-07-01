package com.grupo10.bff_vitacare.dto;

import java.time.LocalDateTime;
import lombok.Data;

/**
 * Espejo de {@code LipidosResponseDto} de {@code measurement-service}.
 */
@Data
public class LipidsDto {

    private Long idControl;

    private Long idPaciente;

    private LocalDateTime fechaHora;

    private String notas;

    private int colesterolTotal;

    private int colesterolLDL;

    private int colesterolHDL;

    private int trigliceridos;

}
