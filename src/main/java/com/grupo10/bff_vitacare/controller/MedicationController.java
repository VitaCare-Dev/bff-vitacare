package com.grupo10.bff_vitacare.controller;

import java.util.List;

import com.grupo10.bff_vitacare.dto.MedicationDto;
import com.grupo10.bff_vitacare.dto.MedicationRequestDto;
import com.grupo10.bff_vitacare.service.MedicationOrchestrationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Gestiona el tratamiento de medicamentos del paciente autenticado.
 */
@RestController
@RequestMapping("/api/medications")
public class MedicationController {

    private final MedicationOrchestrationService medicationOrchestrationService;

    public MedicationController(MedicationOrchestrationService medicationOrchestrationService) {
        this.medicationOrchestrationService = medicationOrchestrationService;
    }

    @PostMapping
    public ResponseEntity<MedicationDto> createMedication(@AuthenticationPrincipal Jwt jwt,
                                                            @RequestBody MedicationRequestDto request) {
        MedicationDto response = medicationOrchestrationService.createMedication(jwt, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<MedicationDto>> listMedications(@AuthenticationPrincipal Jwt jwt,
                                                                 @RequestParam(defaultValue = "false") boolean active) {
        return ResponseEntity.ok(medicationOrchestrationService.listMedications(jwt, active));
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<MedicationDto> deactivateMedication(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id) {
        return ResponseEntity.ok(medicationOrchestrationService.deactivateMedication(jwt, id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMedication(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id) {
        medicationOrchestrationService.deleteMedication(jwt, id);
        return ResponseEntity.noContent().build();
    }

}
