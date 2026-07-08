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

    /**
     * URL de la foto de perfil. En {@code patient-service} se guarda como la
     * URL base del blob (sin SAS); el BFF la reemplaza por una URL firmada de
     * lectura de corta duración antes de devolverla al cliente.
     */
    private String fotoPerfilUrl;

}
