package com.grupo10.bff_vitacare.service;

import com.grupo10.bff_vitacare.client.PatientServiceClient;
import com.grupo10.bff_vitacare.client.UserServiceClient;
import com.grupo10.bff_vitacare.dto.AuthenticatedUserDto;
import com.grupo10.bff_vitacare.dto.PatientDto;
import com.grupo10.bff_vitacare.dto.RegisterPatientRequestDto;
import com.grupo10.bff_vitacare.dto.RegisterPatientResponseDto;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

/**
 * Orquesta el registro completo de un paciente: sincroniza el usuario
 * autenticado en Firebase con {@code user-service} (de forma idempotente) y
 * crea su ficha de paciente en {@code patient-service}.
 */
@Service
public class PatientRegistrationService {

    private static final String DEFAULT_ROLE = "PACIENTE";

    private final UserServiceClient userServiceClient;
    private final PatientServiceClient patientServiceClient;

    /**
     * @param userServiceClient    cliente hacia {@code user-service}
     * @param patientServiceClient cliente hacia {@code patient-service}
     */
    public PatientRegistrationService(UserServiceClient userServiceClient, PatientServiceClient patientServiceClient) {
        this.userServiceClient = userServiceClient;
        this.patientServiceClient = patientServiceClient;
    }

    /**
     * Registra (o continúa el registro de) un paciente para el usuario autenticado.
     *
     * <p>La sincronización del usuario es idempotente: si ya existe (por un
     * intento anterior que falló al crear el paciente), se reutiliza en vez
     * de intentar crearlo de nuevo.</p>
     *
     * @param jwt     ID Token de Firebase ya validado
     * @param request datos del paciente a registrar
     * @return los datos combinados del usuario y el paciente creados
     */
    public RegisterPatientResponseDto registerPatient(Jwt jwt, RegisterPatientRequestDto request) {
        String firebaseUid = jwt.getSubject();
        String correo = jwt.getClaimAsString("email");

        AuthenticatedUserDto user = userServiceClient.tryFindByFirebaseUid(firebaseUid)
                .orElseGet(() -> userServiceClient.createUser(correo, firebaseUid, DEFAULT_ROLE));

        PatientDto patient = patientServiceClient.createPatient(user.getId(), request);

        return toResponse(user, patient);
    }

    /**
     * Combina los datos del usuario y del paciente recién creados en un único DTO de respuesta.
     *
     * @param user    usuario sincronizado
     * @param patient paciente creado
     * @return el DTO combinado para el cliente
     */
    private RegisterPatientResponseDto toResponse(AuthenticatedUserDto user, PatientDto patient) {
        RegisterPatientResponseDto response = new RegisterPatientResponseDto();
        response.setIdUsuario(user.getId());
        response.setCorreo(user.getCorreo());
        response.setRol(user.getRol());
        response.setIdPaciente(patient.getIdPaciente());
        response.setRut(patient.getRut());
        response.setNombre(patient.getNombre());
        response.setApellidoPaterno(patient.getApellidoPaterno());
        response.setApellidoMaterno(patient.getApellidoMaterno());
        response.setFechaNacimiento(patient.getFechaNacimiento());
        response.setTelefonoPrincipal(patient.getTelefonoPrincipal());
        response.setTelefonoSecundario(patient.getTelefonoSecundario());
        return response;
    }

}
