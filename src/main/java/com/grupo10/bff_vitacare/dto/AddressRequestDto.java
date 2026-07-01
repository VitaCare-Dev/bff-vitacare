package com.grupo10.bff_vitacare.dto;

import lombok.Data;

/**
 * Datos de dirección enviados por el paciente autenticado, sin
 * {@code idPaciente}: lo resuelve el BFF a partir del usuario del token.
 */
@Data
public class AddressRequestDto {

    private String calle;

    private String numero;

    private String comuna;

    private String region;

}
