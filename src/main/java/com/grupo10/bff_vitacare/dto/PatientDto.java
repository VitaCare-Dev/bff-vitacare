package com.grupo10.bff_vitacare.dto;

import java.time.LocalDate;
import lombok.Data;

/**
 * Espejo de {@code PatientResponseDto} de {@code patient-service}.
 */
@Data
public class PatientDto {

    private Long idPaciente;

    private Long idUsuario;

    private String rut;

    private String nombre;

    private String apellidoPaterno;

    private String apellidoMaterno;

    private LocalDate fechaNacimiento;

    private String telefonoPrincipal;

    private String telefonoSecundario;

}
