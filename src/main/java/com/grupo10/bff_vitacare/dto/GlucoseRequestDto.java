package com.grupo10.bff_vitacare.dto;

import lombok.Data;

/**
 * Espejo de {@code GlucosaRequestDto} de {@code measurement-service}, sin
 * {@code idPaciente}: lo resuelve el BFF a partir del usuario autenticado.
 */
@Data
public class GlucoseRequestDto {

    private String notas;

    private int glucosa;

    /** Nombre del enum {@code PeriodoGlucosa} de measurement-service (AYUNAS, POSTPRANDIAL, NOCTURNA, ALEATORIO). */
    private String periodo;

}
