package com.grupo10.bff_vitacare.dto;

import java.time.LocalDate;
import lombok.Data;

/**
 * Datos editables del propio perfil de paciente. Actualización parcial:
 * los campos no enviados (o en blanco) se ignoran, igual que en
 * {@code patient-service}. Sin {@code idUsuario} ni {@code rut} — ya están
 * protegidos server-side y no tiene sentido exponerlos aquí.
 */
@Data
public class UpdatePatientRequestDto {

    private String nombre;

    private String apellidoPaterno;

    private String apellidoMaterno;

    private LocalDate fechaNacimiento;

    private String telefonoPrincipal;

    private String telefonoSecundario;

}
