package com.grupo10.bff_vitacare.controller;

import java.util.List;

import com.grupo10.bff_vitacare.client.PatientServiceClient;
import com.grupo10.bff_vitacare.client.UserServiceClient;
import com.grupo10.bff_vitacare.dto.DiseaseDto;
import com.grupo10.bff_vitacare.dto.MedicalThresholdDto;
import com.grupo10.bff_vitacare.dto.PatientDto;
import com.grupo10.bff_vitacare.dto.PhotoUploadUrlDto;
import com.grupo10.bff_vitacare.dto.UpdatePatientRequestDto;
import com.grupo10.bff_vitacare.exception.UpstreamErrorException;
import com.grupo10.bff_vitacare.service.PatientContextService;
import com.grupo10.bff_vitacare.service.ProfilePhotoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
    private final ProfilePhotoService profilePhotoService;

    /**
     * @param patientContextService servicio que resuelve el paciente a partir del token
     * @param patientServiceClient  cliente hacia {@code patient-service}
     * @param userServiceClient     cliente hacia {@code user-service}
     * @param profilePhotoService   servicio que genera URLs firmadas hacia Azure Blob Storage
     */
    public PatientProfileController(PatientContextService patientContextService, PatientServiceClient patientServiceClient,
                                     UserServiceClient userServiceClient, ProfilePhotoService profilePhotoService) {
        this.patientContextService = patientContextService;
        this.patientServiceClient = patientServiceClient;
        this.userServiceClient = userServiceClient;
        this.profilePhotoService = profilePhotoService;
    }

    /**
     * {@code GET /api/patients/me}: devuelve el perfil del paciente autenticado.
     * Si tiene foto de perfil, su URL se reemplaza por una URL firmada de
     * lectura de corta duración antes de responder.
     *
     * @param jwt ID Token de Firebase, inyectado por Spring Security tras validarlo
     * @return 200 con los datos del paciente
     */
    @GetMapping
    public ResponseEntity<PatientDto> getCurrentPatient(@AuthenticationPrincipal Jwt jwt) {
        PatientDto patient = patientContextService.resolveCurrentPatient(jwt);
        if (patient.getFotoPerfilUrl() != null) {
            patient.setFotoPerfilUrl(profilePhotoService.generateReadUrl(patient.getFotoPerfilUrl()));
        }
        return ResponseEntity.ok(patient);
    }

    /**
     * {@code POST /api/patients/me/photo/upload-url}: genera una URL con SAS
     * de escritura para que el paciente suba directamente su foto de perfil
     * a Azure Blob Storage.
     *
     * @param jwt ID Token de Firebase, inyectado por Spring Security tras validarlo
     * @return 200 con la URL de subida, válida por poco tiempo
     */
    @PostMapping("/photo/upload-url")
    public ResponseEntity<PhotoUploadUrlDto> getPhotoUploadUrl(@AuthenticationPrincipal Jwt jwt) {
        PatientDto patient = patientContextService.resolveCurrentPatient(jwt);
        String uploadUrl = profilePhotoService.generateUploadUrl(patient.getIdPaciente());
        return ResponseEntity.ok(new PhotoUploadUrlDto(uploadUrl));
    }

    /**
     * {@code PUT /api/patients/me/photo}: confirma que la foto de perfil ya
     * se subió a Azure y guarda su URL base en {@code patient-service}.
     *
     * <p>Antes de persistir la URL, verifica contra Azure Blob Storage que el
     * archivo realmente exista; si la subida nunca se completó (o falló a
     * medio camino), rechaza la confirmación en vez de dejar al paciente con
     * una foto de perfil rota.
     *
     * @param jwt ID Token de Firebase, inyectado por Spring Security tras validarlo
     * @return 200 con el paciente actualizado (la foto ya con una URL de lectura firmada)
     * @throws UpstreamErrorException con 409 si el blob todavía no existe en Azure
     */
    @PutMapping("/photo")
    public ResponseEntity<PatientDto> confirmPhotoUpload(@AuthenticationPrincipal Jwt jwt) {
        PatientDto patient = patientContextService.resolveCurrentPatient(jwt);

        if (!profilePhotoService.blobExists(patient.getIdPaciente())) {
            throw new UpstreamErrorException(HttpStatus.CONFLICT,
                    "La foto de perfil aún no terminó de subirse. Inténtalo nuevamente.");
        }

        String baseUrl = profilePhotoService.getBaseBlobUrl(patient.getIdPaciente());
        PatientDto updated = patientServiceClient.updatePhotoUrl(patient.getIdPaciente(), baseUrl);
        updated.setFotoPerfilUrl(profilePhotoService.generateReadUrl(baseUrl));
        return ResponseEntity.ok(updated);
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
     * {@code DELETE /api/patients/me/diseases/{idEnfermedad}}: elimina una
     * enfermedad crónica del paciente autenticado y recalcula sus umbrales médicos.
     *
     * @param jwt          ID Token de Firebase, inyectado por Spring Security tras validarlo
     * @param idEnfermedad identificador de la enfermedad a eliminar
     * @return 204 sin contenido
     */
    @DeleteMapping("/diseases/{idEnfermedad}")
    public ResponseEntity<Void> removeCurrentPatientDisease(@AuthenticationPrincipal Jwt jwt,
                                                             @PathVariable Long idEnfermedad) {
        PatientDto patient = patientContextService.resolveCurrentPatient(jwt);
        patientServiceClient.removeDisease(patient.getIdPaciente(), idEnfermedad);
        return ResponseEntity.noContent().build();
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
