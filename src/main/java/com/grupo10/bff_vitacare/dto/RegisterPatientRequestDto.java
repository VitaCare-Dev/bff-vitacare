package com.grupo10.bff_vitacare.dto;

import java.time.LocalDate;
import lombok.Data;

/**
 * Datos del paciente enviados por el frontend tras el signup en Firebase.
 * El {@code firebaseUid} y el {@code correo} no viajan aquí: se toman del
 * ID Token ya validado, nunca del body.
 */
@Data
public class RegisterPatientRequestDto {

    private String rut;

    private String nombre;

    private String apellidoPaterno;

    private String apellidoMaterno;

    private LocalDate fechaNacimiento;

    private String telefonoPrincipal;

    private String telefonoSecundario;

}
