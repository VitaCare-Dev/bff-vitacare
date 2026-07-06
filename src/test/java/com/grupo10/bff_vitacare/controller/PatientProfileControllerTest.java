package com.grupo10.bff_vitacare.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.grupo10.bff_vitacare.client.PatientServiceClient;
import com.grupo10.bff_vitacare.client.UserServiceClient;
import com.grupo10.bff_vitacare.dto.DiseaseDto;
import com.grupo10.bff_vitacare.dto.MedicalThresholdDto;
import com.grupo10.bff_vitacare.dto.PatientDto;
import com.grupo10.bff_vitacare.dto.UpdatePatientRequestDto;
import com.grupo10.bff_vitacare.service.PatientContextService;
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

@ExtendWith(MockitoExtension.class)
class PatientProfileControllerTest {

    @Mock
    private PatientContextService patientContextService;

    @Mock
    private PatientServiceClient patientServiceClient;

    @Mock
    private UserServiceClient userServiceClient;

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
