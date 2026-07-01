package com.grupo10.bff_vitacare.dto;

import java.time.LocalDate;
import lombok.Data;

/**
 * Espejo de {@code MedicationResponseDto} de {@code medication-service}.
 */
@Data
public class MedicationDto {

    private Long idMedicamento;

    private Long idPaciente;

    private String nombreMedicamento;

    private String dosis;

    private int frecuenciaHoras;

    private LocalDate fechaInicio;

    private LocalDate fechaTermino;

    private int activo;

}
