package com.grupo10.bff_vitacare.dto;

import lombok.Data;

/**
 * Espejo de {@code MedicionVitalRequestDto} de {@code measurement-service}, sin
 * {@code idPaciente}: lo resuelve el BFF a partir del usuario autenticado.
 */
@Data
public class VitalsRequestDto {

    private String notas;

    private Integer presionSistolica;

    private Integer presionDiastolica;

    private double temperatura;

    private double peso;

}
