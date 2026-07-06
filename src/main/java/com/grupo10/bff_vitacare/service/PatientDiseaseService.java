package com.grupo10.bff_vitacare.service;

import com.grupo10.bff_vitacare.client.PatientServiceClient;
import com.grupo10.bff_vitacare.dto.PatientDto;
import com.grupo10.bff_vitacare.dto.RegisterDiseaseRequestDto;
import com.grupo10.bff_vitacare.exception.PatientNotFoundException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

/**
 * Orquesta la asociación de una enfermedad crónica al paciente autenticado,
 * resolviendo su {@code idPaciente} a partir del token en vez de confiar en
 * un valor enviado por el cliente.
 */
@Service
public class PatientDiseaseService {

    private final PatientContextService patientContextService;
    private final PatientServiceClient patientServiceClient;

    /**
     * @param patientContextService servicio que resuelve el paciente a partir del token
     * @param patientServiceClient  cliente hacia {@code patient-service}
     */
    public PatientDiseaseService(PatientContextService patientContextService, PatientServiceClient patientServiceClient) {
        this.patientContextService = patientContextService;
        this.patientServiceClient = patientServiceClient;
    }

    /**
     * Asocia una enfermedad crónica al paciente del usuario autenticado.
     *
     * @param jwt     ID Token de Firebase ya validado
     * @param request enfermedad a asociar
     * @throws PatientNotFoundException si el usuario todavía no completó su registro de paciente
     */
    public void registerDiseaseForCurrentUser(Jwt jwt, RegisterDiseaseRequestDto request) {
        PatientDto patient = patientContextService.resolveCurrentPatient(jwt);
        patientServiceClient.registerDisease(patient.getIdPaciente(), request.getIdEnfermedad());
    }

}
