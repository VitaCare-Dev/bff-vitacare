package com.grupo10.bff_vitacare.dto;

import java.time.LocalDate;
import lombok.Data;

/**
 * Espejo de {@code MedicationRequestDto} de {@code medication-service}, sin
 * {@code idPaciente} ni {@code activo}: el BFF resuelve el paciente a partir
 * del usuario autenticado y siempre crea el tratamiento activo.
 */
@Data
public class MedicationRequestDto {

    private String nombreMedicamento;

    private String dosis;

    private int frecuenciaHoras;

    private LocalDate fechaInicio;

    private LocalDate fechaTermino;

}
