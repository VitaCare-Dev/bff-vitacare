package com.grupo10.bff_vitacare.dto;

import lombok.Data;

/**
 * Datos enviados por el frontend para asociar una enfermedad crónica al
 * paciente autenticado. El {@code idPaciente} no viaja aquí: lo resuelve el
 * BFF a partir del usuario del token, nunca se confía en un valor del cliente.
 */
@Data
public class RegisterDiseaseRequestDto {

    private Long idEnfermedad;

}
