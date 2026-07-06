package com.grupo10.bff_vitacare.service;

import com.grupo10.bff_vitacare.client.PatientServiceClient;
import com.grupo10.bff_vitacare.dto.AuthenticatedUserDto;
import com.grupo10.bff_vitacare.dto.PatientDto;
import com.grupo10.bff_vitacare.exception.PatientNotFoundException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

/**
 * Resuelve el paciente asociado al usuario autenticado (token Firebase →
 * {@code id_usuario} → {@code id_paciente}), para que ningún endpoint tenga
 * que confiar en un {@code idPaciente} enviado por el cliente.
 */
@Service
public class PatientContextService {

    private final AuthContextService authContextService;
    private final PatientServiceClient patientServiceClient;

    /**
     * @param authContextService   servicio que resuelve el usuario a partir del token
     * @param patientServiceClient cliente hacia {@code patient-service}
     */
    public PatientContextService(AuthContextService authContextService, PatientServiceClient patientServiceClient) {
        this.authContextService = authContextService;
        this.patientServiceClient = patientServiceClient;
    }

    /**
     * Resuelve el paciente del usuario autenticado.
     *
     * @param jwt ID Token de Firebase ya validado
     * @return el paciente asociado
     * @throws PatientNotFoundException si el usuario todavía no completó su registro de paciente
     */
    public PatientDto resolveCurrentPatient(Jwt jwt) {
        AuthenticatedUserDto user = authContextService.resolveCurrentUser(jwt);
        return patientServiceClient.findByUserId(user.getId());
    }

}
