package com.grupo10.bff_vitacare.controller;

import java.util.List;

import com.grupo10.bff_vitacare.dto.GlucoseDto;
import com.grupo10.bff_vitacare.dto.GlucoseRequestDto;
import com.grupo10.bff_vitacare.dto.HealthControlDto;
import com.grupo10.bff_vitacare.dto.LipidsDto;
import com.grupo10.bff_vitacare.dto.LipidsRequestDto;
import com.grupo10.bff_vitacare.dto.VitalsDto;
import com.grupo10.bff_vitacare.dto.VitalsRequestDto;
import com.grupo10.bff_vitacare.service.MeasurementOrchestrationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Registra y consulta las mediciones de salud del paciente autenticado.
 */
@RestController
@RequestMapping("/api/measurements")
public class MeasurementController {

    private final MeasurementOrchestrationService measurementOrchestrationService;

    public MeasurementController(MeasurementOrchestrationService measurementOrchestrationService) {
        this.measurementOrchestrationService = measurementOrchestrationService;
    }

    @PostMapping("/glucose")
    public ResponseEntity<GlucoseDto> createGlucose(@AuthenticationPrincipal Jwt jwt,
                                                     @RequestBody GlucoseRequestDto request) {
        GlucoseDto response = measurementOrchestrationService.createGlucose(jwt, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/glucose")
    public ResponseEntity<List<GlucoseDto>> listGlucose(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(measurementOrchestrationService.listGlucose(jwt));
    }

    @GetMapping("/glucose/latest")
    public ResponseEntity<GlucoseDto> getLatestGlucose(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(measurementOrchestrationService.getLatestGlucose(jwt));
    }

    @GetMapping("/glucose/{id}")
    public ResponseEntity<GlucoseDto> getGlucoseById(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id) {
        return ResponseEntity.ok(measurementOrchestrationService.getGlucoseById(jwt, id));
    }

    @DeleteMapping("/glucose/{id}")
    public ResponseEntity<Void> deleteGlucose(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id) {
        measurementOrchestrationService.deleteGlucose(jwt, id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/lipids")
    public ResponseEntity<LipidsDto> createLipids(@AuthenticationPrincipal Jwt jwt,
                                                   @RequestBody LipidsRequestDto request) {
        LipidsDto response = measurementOrchestrationService.createLipids(jwt, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/lipids")
    public ResponseEntity<List<LipidsDto>> listLipids(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(measurementOrchestrationService.listLipids(jwt));
    }

    @GetMapping("/lipids/latest")
    public ResponseEntity<LipidsDto> getLatestLipids(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(measurementOrchestrationService.getLatestLipids(jwt));
    }

    @GetMapping("/lipids/{id}")
    public ResponseEntity<LipidsDto> getLipidsById(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id) {
        return ResponseEntity.ok(measurementOrchestrationService.getLipidsById(jwt, id));
    }

    @DeleteMapping("/lipids/{id}")
    public ResponseEntity<Void> deleteLipids(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id) {
        measurementOrchestrationService.deleteLipids(jwt, id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/vitals")
    public ResponseEntity<VitalsDto> createVitals(@AuthenticationPrincipal Jwt jwt,
                                                   @RequestBody VitalsRequestDto request) {
        VitalsDto response = measurementOrchestrationService.createVitals(jwt, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/vitals")
    public ResponseEntity<List<VitalsDto>> listVitals(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(measurementOrchestrationService.listVitals(jwt));
    }

    @GetMapping("/vitals/latest")
    public ResponseEntity<VitalsDto> getLatestVitals(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(measurementOrchestrationService.getLatestVitals(jwt));
    }

    @GetMapping("/vitals/{id}")
    public ResponseEntity<VitalsDto> getVitalsById(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id) {
        return ResponseEntity.ok(measurementOrchestrationService.getVitalsById(jwt, id));
    }

    @DeleteMapping("/vitals/{id}")
    public ResponseEntity<Void> deleteVitals(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id) {
        measurementOrchestrationService.deleteVitals(jwt, id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/history")
    public ResponseEntity<List<HealthControlDto>> getHistory(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(measurementOrchestrationService.getHealthHistory(jwt));
    }

}
