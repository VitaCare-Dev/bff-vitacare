package com.grupo10.bff_vitacare.controller;

import com.grupo10.bff_vitacare.dto.RegisterPatientRequestDto;
import com.grupo10.bff_vitacare.dto.RegisterPatientResponseDto;
import com.grupo10.bff_vitacare.service.PatientRegistrationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Orquesta el registro completo de un paciente tras el signup en Firebase.
 */
@RestController
@RequestMapping("/api/auth")
public class RegistrationController {

    private final PatientRegistrationService patientRegistrationService;

    /**
     * @param patientRegistrationService servicio que orquesta el registro de pacientes
     */
    public RegistrationController(PatientRegistrationService patientRegistrationService) {
        this.patientRegistrationService = patientRegistrationService;
    }

    /**
     * {@code POST /api/auth/register}: registra (o continúa el registro de) el
     * paciente asociado al usuario autenticado.
     *
     * @param jwt     ID Token de Firebase, inyectado por Spring Security tras validarlo
     * @param request datos del paciente a registrar
     * @return 201 con los datos combinados de usuario y paciente creados
     */
    @PostMapping("/register")
    public ResponseEntity<RegisterPatientResponseDto> register(@AuthenticationPrincipal Jwt jwt,
                                                                 @RequestBody RegisterPatientRequestDto request) {
        RegisterPatientResponseDto response = patientRegistrationService.registerPatient(jwt, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

}
