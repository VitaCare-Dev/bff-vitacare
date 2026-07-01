package com.grupo10.bff_vitacare.dto;

import lombok.Data;

/**
 * Espejo de {@code AddressResponseDto} de {@code patient-service}.
 */
@Data
public class AddressDto {

    private Long idDireccion;

    private Long idPaciente;

    private String calle;

    private String numero;

    private String comuna;

    private String region;

}
