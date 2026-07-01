package com.grupo10.bff_vitacare.dto;

import lombok.Data;

/**
 * Espejo de {@code LipidosRequestDto} de {@code measurement-service}, sin
 * {@code idPaciente}: lo resuelve el BFF a partir del usuario autenticado.
 */
@Data
public class LipidsRequestDto {

    private String notas;

    private int colesterolTotal;

    private int colesterolLDL;

    private int colesterolHDL;

    private int trigliceridos;

}
