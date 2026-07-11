package com.grupo10.bff_vitacare.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class PatientProfileControllerTest {

    @Mock
    private PatientContextService patientContextService;

    @Mock
    private PatientServiceClient patientServiceClient;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private ProfilePhotoService profilePhotoService;

    @InjectMocks
    private PatientProfileController patientProfileController;

    private Jwt jwt;
    private PatientDto patient;

    @BeforeEach
    void setUp() {
        jwt = Jwt.withTokenValue("token").header("alg", "none").claim("sub", "uid-1").build();
        patient = new PatientDto();
        patient.setIdPaciente(1L);
    }

    @Test
    void getCurrentPatientReturnsThePatient() {
        when(patientContextService.resolveCurrentPatient(jwt)).thenReturn(patient);

        ResponseEntity<PatientDto> response = patientProfileController.getCurrentPatient(jwt);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getIdPaciente()).isEqualTo(1L);
    }

    @Test
    void getCurrentPatientDoesNotCallProfilePhotoServiceWhenThereIsNoPhoto() {
        when(patientContextService.resolveCurrentPatient(jwt)).thenReturn(patient);

        patientProfileController.getCurrentPatient(jwt);

        verify(profilePhotoService, never()).generateReadUrl(any());
    }

    @Test
    void getCurrentPatientReplacesTheBaseUrlWithASignedReadUrlWhenThereIsAPhoto() {
        patient.setFotoPerfilUrl("https://vitacareprofilephotos.blob.core.windows.net/profile-photos/paciente-1.jpg");
        when(patientContextService.resolveCurrentPatient(jwt)).thenReturn(patient);
        when(profilePhotoService.generateReadUrl(patient.getFotoPerfilUrl()))
                .thenReturn("https://vitacareprofilephotos.blob.core.windows.net/profile-photos/paciente-1.jpg?sig=abc");

        ResponseEntity<PatientDto> response = patientProfileController.getCurrentPatient(jwt);

        assertThat(response.getBody().getFotoPerfilUrl()).endsWith("?sig=abc");
    }

    @Test
    void getPhotoUploadUrlReturnsTheGeneratedUrl() {
        when(patientContextService.resolveCurrentPatient(jwt)).thenReturn(patient);
        when(profilePhotoService.generateUploadUrl(1L)).thenReturn("https://example.blob/upload?sig=xyz");

        ResponseEntity<PhotoUploadUrlDto> response = patientProfileController.getPhotoUploadUrl(jwt);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getUploadUrl()).isEqualTo("https://example.blob/upload?sig=xyz");
    }

    @Test
    void confirmPhotoUploadPersistsTheBaseUrlAndReturnsASignedReadUrl() {
        when(patientContextService.resolveCurrentPatient(jwt)).thenReturn(patient);
        when(profilePhotoService.blobExists(1L)).thenReturn(true);
        when(profilePhotoService.getBaseBlobUrl(1L)).thenReturn("https://example.blob/paciente-1.jpg");
        PatientDto updated = new PatientDto();
        updated.setIdPaciente(1L);
        when(patientServiceClient.updatePhotoUrl(1L, "https://example.blob/paciente-1.jpg")).thenReturn(updated);
        when(profilePhotoService.generateReadUrl("https://example.blob/paciente-1.jpg"))
                .thenReturn("https://example.blob/paciente-1.jpg?sig=abc");

        ResponseEntity<PatientDto> response = patientProfileController.confirmPhotoUpload(jwt);

        assertThat(response.getBody().getFotoPerfilUrl()).isEqualTo("https://example.blob/paciente-1.jpg?sig=abc");
    }

    @Test
    void confirmPhotoUploadThrowsConflictWhenTheBlobDoesNotExistYet() {
        when(patientContextService.resolveCurrentPatient(jwt)).thenReturn(patient);
        when(profilePhotoService.blobExists(1L)).thenReturn(false);

        assertThatThrownBy(() -> patientProfileController.confirmPhotoUpload(jwt))
                .isInstanceOf(UpstreamErrorException.class)
                .satisfies(ex -> assertThat(((UpstreamErrorException) ex).getStatus()).isEqualTo(HttpStatus.CONFLICT));
        verify(patientServiceClient, never()).updatePhotoUrl(any(), any());
    }

    @Test
    void getCurrentPatientThresholdsReturnsTheThresholds() {
        when(patientContextService.resolveCurrentPatient(jwt)).thenReturn(patient);
        MedicalThresholdDto thresholds = new MedicalThresholdDto();
        thresholds.setGlucosaMax(180);
        when(patientServiceClient.getThresholds(1L)).thenReturn(thresholds);

        ResponseEntity<MedicalThresholdDto> response = patientProfileController.getCurrentPatientThresholds(jwt);

        assertThat(response.getBody().getGlucosaMax()).isEqualTo(180);
    }

    @Test
    void getCurrentPatientDiseasesReturnsTheDiseases() {
        when(patientContextService.resolveCurrentPatient(jwt)).thenReturn(patient);
        when(patientServiceClient.getPatientDiseases(1L)).thenReturn(List.of(new DiseaseDto()));

        ResponseEntity<List<DiseaseDto>> response = patientProfileController.getCurrentPatientDiseases(jwt);

        assertThat(response.getBody()).hasSize(1);
    }

    @Test
    void updateCurrentPatientReturnsTheUpdatedPatient() {
        when(patientContextService.resolveCurrentPatient(jwt)).thenReturn(patient);
        UpdatePatientRequestDto request = new UpdatePatientRequestDto();
        PatientDto updated = new PatientDto();
        updated.setNombre("María José");
        when(patientServiceClient.updatePatient(1L, request)).thenReturn(updated);

        ResponseEntity<PatientDto> response = patientProfileController.updateCurrentPatient(jwt, request);

        assertThat(response.getBody().getNombre()).isEqualTo("María José");
    }

    @Test
    void deleteCurrentPatientDeletesFromBothServices() {
        when(patientContextService.resolveCurrentPatient(jwt)).thenReturn(patient);

        ResponseEntity<Void> response = patientProfileController.deleteCurrentPatient(jwt);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(patientServiceClient).deletePatient(1L);
        verify(userServiceClient).deleteUserByFirebaseUid("uid-1");
    }
}
