package com.grupo10.bff_vitacare.dto;

import lombok.Data;

/**
 * Espejo de {@code MedicalThresholdResponseDto} de {@code patient-service}.
 */
@Data
public class MedicalThresholdDto {

    private Long idUmbral;

    private Long idPaciente;

    private Integer glucosaMax;

    private Integer glucosaMin;

    private Integer sistolicaMax;

    private Integer diastolicaMax;

    private Double temperaturaMax;

}
