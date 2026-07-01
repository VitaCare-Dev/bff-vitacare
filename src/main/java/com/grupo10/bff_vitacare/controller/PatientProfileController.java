package com.grupo10.bff_vitacare.controller;

import com.grupo10.bff_vitacare.client.PatientServiceClient;
import com.grupo10.bff_vitacare.dto.MedicalThresholdDto;
import com.grupo10.bff_vitacare.dto.PatientDto;
import com.grupo10.bff_vitacare.dto.UpdatePatientRequestDto;
import com.grupo10.bff_vitacare.service.PatientContextService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Expone el perfil del paciente autenticado y sus umbrales médicos.
 */
@RestController
@RequestMapping("/api/patients/me")
public class PatientProfileController {

    private final PatientContextService patientContextService;
    private final PatientServiceClient patientServiceClient;

    public PatientProfileController(PatientContextService patientContextService, PatientServiceClient patientServiceClient) {
        this.patientContextService = patientContextService;
        this.patientServiceClient = patientServiceClient;
    }

    @GetMapping
    public ResponseEntity<PatientDto> getCurrentPatient(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(patientContextService.resolveCurrentPatient(jwt));
    }

    @GetMapping("/thresholds")
    public ResponseEntity<MedicalThresholdDto> getCurrentPatientThresholds(@AuthenticationPrincipal Jwt jwt) {
        PatientDto patient = patientContextService.resolveCurrentPatient(jwt);
        return ResponseEntity.ok(patientServiceClient.getThresholds(patient.getIdPaciente()));
    }

    @PutMapping
    public ResponseEntity<PatientDto> updateCurrentPatient(@AuthenticationPrincipal Jwt jwt,
                                                            @RequestBody UpdatePatientRequestDto request) {
        PatientDto patient = patientContextService.resolveCurrentPatient(jwt);
        return ResponseEntity.ok(patientServiceClient.updatePatient(patient.getIdPaciente(), request));
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteCurrentPatient(@AuthenticationPrincipal Jwt jwt) {
        PatientDto patient = patientContextService.resolveCurrentPatient(jwt);
        patientServiceClient.deletePatient(patient.getIdPaciente());
        return ResponseEntity.noContent().build();
    }

}
