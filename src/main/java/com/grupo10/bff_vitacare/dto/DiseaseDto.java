package com.grupo10.bff_vitacare.dto;

import lombok.Data;

/**
 * Espejo de {@code DiseaseResponseDto} de {@code patient-service}.
 */
@Data
public class DiseaseDto {

    private Long idEnfermedad;

    private String nombreEnfermedad;

    private String descripcion;

}
