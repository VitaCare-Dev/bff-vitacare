package com.grupo10.bff_vitacare.dto;

import java.time.LocalDateTime;
import lombok.Data;

/**
 * Espejo de {@code GlucosaResponseDto} de {@code measurement-service}.
 */
@Data
public class GlucoseDto {

    private Long idControl;

    private Long idPaciente;

    private LocalDateTime fechaHora;

    private String notas;

    private int glucosa;

    private String periodo;

}
