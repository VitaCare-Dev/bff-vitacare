package com.grupo10.bff_vitacare.controller;

import java.util.List;

import com.grupo10.bff_vitacare.client.PatientServiceClient;
import com.grupo10.bff_vitacare.client.UserServiceClient;
import com.grupo10.bff_vitacare.dto.DiseaseDto;
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
    private final UserServiceClient userServiceClient;

    /**
     * @param patientContextService servicio que resuelve el paciente a partir del token
     * @param patientServiceClient  cliente hacia {@code patient-service}
     * @param userServiceClient     cliente hacia {@code user-service}
     */
    public PatientProfileController(PatientContextService patientContextService, PatientServiceClient patientServiceClient,
                                     UserServiceClient userServiceClient) {
        this.patientContextService = patientContextService;
        this.patientServiceClient = patientServiceClient;
        this.userServiceClient = userServiceClient;
    }

    /**
     * {@code GET /api/patients/me}: devuelve el perfil del paciente autenticado.
     *
     * @param jwt ID Token de Firebase, inyectado por Spring Security tras validarlo
     * @return 200 con los datos del paciente
     */
    @GetMapping
    public ResponseEntity<PatientDto> getCurrentPatient(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(patientContextService.resolveCurrentPatient(jwt));
    }

    /**
     * {@code GET /api/patients/me/thresholds}: devuelve los umbrales médicos
     * derivados de la(s) enfermedad(es) crónica(s) del paciente autenticado.
     *
     * @param jwt ID Token de Firebase, inyectado por Spring Security tras validarlo
     * @return 200 con los umbrales médicos
     */
    @GetMapping("/thresholds")
    public ResponseEntity<MedicalThresholdDto> getCurrentPatientThresholds(@AuthenticationPrincipal Jwt jwt) {
        PatientDto patient = patientContextService.resolveCurrentPatient(jwt);
        return ResponseEntity.ok(patientServiceClient.getThresholds(patient.getIdPaciente()));
    }

    /**
     * {@code GET /api/patients/me/diseases}: lista las enfermedades crónicas
     * del paciente autenticado.
     *
     * @param jwt ID Token de Firebase, inyectado por Spring Security tras validarlo
     * @return 200 con las enfermedades del paciente
     */
    @GetMapping("/diseases")
    public ResponseEntity<List<DiseaseDto>> getCurrentPatientDiseases(@AuthenticationPrincipal Jwt jwt) {
        PatientDto patient = patientContextService.resolveCurrentPatient(jwt);
        return ResponseEntity.ok(patientServiceClient.getPatientDiseases(patient.getIdPaciente()));
    }

    /**
     * {@code PUT /api/patients/me}: actualiza parcialmente los datos del
     * paciente autenticado.
     *
     * @param jwt     ID Token de Firebase, inyectado por Spring Security tras validarlo
     * @param request campos a actualizar
     * @return 200 con el paciente actualizado
     */
    @PutMapping
    public ResponseEntity<PatientDto> updateCurrentPatient(@AuthenticationPrincipal Jwt jwt,
                                                            @RequestBody UpdatePatientRequestDto request) {
        PatientDto patient = patientContextService.resolveCurrentPatient(jwt);
        return ResponseEntity.ok(patientServiceClient.updatePatient(patient.getIdPaciente(), request));
    }

    /**
     * {@code DELETE /api/patients/me}: elimina la cuenta del paciente
     * autenticado, primero en {@code patient-service} (cuya base de datos cae
     * en cascada hacia sus datos asociados) y luego en {@code user-service}.
     *
     * @param jwt ID Token de Firebase, inyectado por Spring Security tras validarlo
     * @return 204 sin contenido
     */
    @DeleteMapping
    public ResponseEntity<Void> deleteCurrentPatient(@AuthenticationPrincipal Jwt jwt) {
        PatientDto patient = patientContextService.resolveCurrentPatient(jwt);
        patientServiceClient.deletePatient(patient.getIdPaciente());
        userServiceClient.deleteUserByFirebaseUid(jwt.getSubject());
        return ResponseEntity.noContent().build();
    }

}
