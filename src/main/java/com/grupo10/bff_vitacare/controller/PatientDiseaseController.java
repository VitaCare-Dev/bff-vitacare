package com.grupo10.bff_vitacare.controller;

import com.grupo10.bff_vitacare.dto.RegisterDiseaseRequestDto;
import com.grupo10.bff_vitacare.service.PatientDiseaseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Asocia enfermedades crónicas al paciente autenticado.
 */
@RestController
@RequestMapping("/api/patients/me")
public class PatientDiseaseController {

    private final PatientDiseaseService patientDiseaseService;

    public PatientDiseaseController(PatientDiseaseService patientDiseaseService) {
        this.patientDiseaseService = patientDiseaseService;
    }

    @PostMapping("/diseases")
    public ResponseEntity<Void> registerDisease(@AuthenticationPrincipal Jwt jwt,
                                                 @RequestBody RegisterDiseaseRequestDto request) {
        patientDiseaseService.registerDiseaseForCurrentUser(jwt, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
