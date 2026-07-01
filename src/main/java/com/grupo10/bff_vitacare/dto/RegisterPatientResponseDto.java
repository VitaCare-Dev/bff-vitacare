package com.grupo10.bff_vitacare.dto;

import java.time.LocalDate;
import lombok.Data;

/**
 * Respuesta combinada del registro completo: datos del usuario sincronizado
 * en {@code user-service} y del paciente creado en {@code patient-service}.
 */
@Data
public class RegisterPatientResponseDto {

    private Long idUsuario;

    private String correo;

    private String rol;

    private Long idPaciente;

    private String rut;

    private String nombre;

    private String apellidoPaterno;

    private String apellidoMaterno;

    private LocalDate fechaNacimiento;

    private String telefonoPrincipal;

    private String telefonoSecundario;

}
