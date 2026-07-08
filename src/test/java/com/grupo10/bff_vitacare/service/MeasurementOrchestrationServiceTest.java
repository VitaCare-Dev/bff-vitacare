package com.grupo10.bff_vitacare.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.grupo10.bff_vitacare.client.MeasurementServiceClient;
import com.grupo10.bff_vitacare.dto.GlucoseDto;
import com.grupo10.bff_vitacare.dto.GlucoseRequestDto;
import com.grupo10.bff_vitacare.dto.HealthControlDto;
import com.grupo10.bff_vitacare.dto.LipidsDto;
import com.grupo10.bff_vitacare.dto.LipidsRequestDto;
import com.grupo10.bff_vitacare.dto.PageResponseDto;
import com.grupo10.bff_vitacare.dto.PatientDto;
import com.grupo10.bff_vitacare.dto.VitalsDto;
import com.grupo10.bff_vitacare.dto.VitalsRequestDto;
import com.grupo10.bff_vitacare.exception.UpstreamErrorException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;

@ExtendWith(MockitoExtension.class)
class MeasurementOrchestrationServiceTest {

    @Mock
    private PatientContextService patientContextService;

    @Mock
    private MeasurementServiceClient measurementServiceClient;

    @InjectMocks
    private MeasurementOrchestrationService measurementOrchestrationService;

    private Jwt jwt;

    @BeforeEach
    void setUp() {
        jwt = Jwt.withTokenValue("token").header("alg", "none").claim("sub", "uid-1").build();
        PatientDto patient = new PatientDto();
        patient.setIdPaciente(1L);
        lenient().when(patientContextService.resolveCurrentPatient(jwt)).thenReturn(patient);
    }

    @Test
    void createGlucoseDelegatesWithResolvedPatientId() {
        GlucoseRequestDto request = new GlucoseRequestDto();
        GlucoseDto created = new GlucoseDto();
        created.setIdControl(1L);
        when(measurementServiceClient.createGlucose(1L, request)).thenReturn(created);

        GlucoseDto result = measurementOrchestrationService.createGlucose(jwt, request);

        assertThat(result.getIdControl()).isEqualTo(1L);
    }

    @Test
    void listGlucoseDelegatesToTheClient() {
        PageResponseDto<GlucoseDto> page = new PageResponseDto<>(List.of(new GlucoseDto()), 0, 10, 1, 1);
        when(measurementServiceClient.listGlucoseByPatient(1L, 0, 10, null, null)).thenReturn(page);

        assertThat(measurementOrchestrationService.listGlucose(jwt, 0, 10, null, null).getContent()).hasSize(1);
    }

    @Test
    void getLatestGlucoseDelegatesToTheClient() {
        GlucoseDto latest = new GlucoseDto();
        latest.setGlucosa(100);
        when(measurementServiceClient.getLatestGlucose(1L)).thenReturn(latest);

        assertThat(measurementOrchestrationService.getLatestGlucose(jwt).getGlucosa()).isEqualTo(100);
    }

    @Test
    void createLipidsDelegatesWithResolvedPatientId() {
        LipidsRequestDto request = new LipidsRequestDto();
        LipidsDto created = new LipidsDto();
        created.setIdControl(1L);
        when(measurementServiceClient.createLipids(1L, request)).thenReturn(created);

        assertThat(measurementOrchestrationService.createLipids(jwt, request).getIdControl()).isEqualTo(1L);
    }

    @Test
    void listLipidsDelegatesToTheClient() {
        PageResponseDto<LipidsDto> page = new PageResponseDto<>(List.of(new LipidsDto()), 0, 10, 1, 1);
        when(measurementServiceClient.listLipidsByPatient(1L, 0, 10, null, null)).thenReturn(page);

        assertThat(measurementOrchestrationService.listLipids(jwt, 0, 10, null, null).getContent()).hasSize(1);
    }

    @Test
    void getLatestLipidsDelegatesToTheClient() {
        when(measurementServiceClient.getLatestLipids(1L)).thenReturn(new LipidsDto());

        assertThat(measurementOrchestrationService.getLatestLipids(jwt)).isNotNull();
    }

    @Test
    void createVitalsDelegatesWithResolvedPatientId() {
        VitalsRequestDto request = new VitalsRequestDto();
        VitalsDto created = new VitalsDto();
        created.setIdControl(1L);
        when(measurementServiceClient.createVitals(1L, request)).thenReturn(created);

        assertThat(measurementOrchestrationService.createVitals(jwt, request).getIdControl()).isEqualTo(1L);
    }

    @Test
    void listVitalsDelegatesToTheClient() {
        PageResponseDto<VitalsDto> page = new PageResponseDto<>(List.of(new VitalsDto()), 0, 10, 1, 1);
        when(measurementServiceClient.listVitalsByPatient(1L, 0, 10, null, null)).thenReturn(page);

        assertThat(measurementOrchestrationService.listVitals(jwt, 0, 10, null, null).getContent()).hasSize(1);
    }

    @Test
    void getLatestVitalsDelegatesToTheClient() {
        when(measurementServiceClient.getLatestVitals(1L)).thenReturn(new VitalsDto());

        assertThat(measurementOrchestrationService.getLatestVitals(jwt)).isNotNull();
    }

    @Test
    void getHealthHistoryDelegatesToTheClient() {
        when(measurementServiceClient.getHealthHistory(1L)).thenReturn(List.of(new HealthControlDto()));

        assertThat(measurementOrchestrationService.getHealthHistory(jwt)).hasSize(1);
    }

    @Test
    void getGlucoseByIdSucceedsWhenOwnedByCurrentPatient() {
        GlucoseDto glucose = new GlucoseDto();
        glucose.setIdPaciente(1L);
        when(measurementServiceClient.getGlucoseById(10L)).thenReturn(glucose);

        assertThat(measurementOrchestrationService.getGlucoseById(jwt, 10L)).isEqualTo(glucose);
    }

    @Test
    void getGlucoseByIdThrowsWhenOwnedByAnotherPatient() {
        GlucoseDto glucose = new GlucoseDto();
        glucose.setIdPaciente(99L);
        when(measurementServiceClient.getGlucoseById(10L)).thenReturn(glucose);

        assertThatThrownBy(() -> measurementOrchestrationService.getGlucoseById(jwt, 10L))
                .isInstanceOf(UpstreamErrorException.class);
    }

    @Test
    void deleteGlucoseSucceedsWhenOwnedByCurrentPatient() {
        GlucoseDto glucose = new GlucoseDto();
        glucose.setIdPaciente(1L);
        when(measurementServiceClient.getGlucoseById(10L)).thenReturn(glucose);

        measurementOrchestrationService.deleteGlucose(jwt, 10L);

        verify(measurementServiceClient).deleteGlucose(10L);
    }

    @Test
    void deleteGlucoseThrowsWhenOwnedByAnotherPatient() {
        GlucoseDto glucose = new GlucoseDto();
        glucose.setIdPaciente(99L);
        when(measurementServiceClient.getGlucoseById(10L)).thenReturn(glucose);

        assertThatThrownBy(() -> measurementOrchestrationService.deleteGlucose(jwt, 10L))
                .isInstanceOf(UpstreamErrorException.class);
    }

    @Test
    void getLipidsByIdSucceedsWhenOwnedByCurrentPatient() {
        LipidsDto lipids = new LipidsDto();
        lipids.setIdPaciente(1L);
        when(measurementServiceClient.getLipidsById(10L)).thenReturn(lipids);

        assertThat(measurementOrchestrationService.getLipidsById(jwt, 10L)).isEqualTo(lipids);
    }

    @Test
    void getLipidsByIdThrowsWhenOwnedByAnotherPatient() {
        LipidsDto lipids = new LipidsDto();
        lipids.setIdPaciente(99L);
        when(measurementServiceClient.getLipidsById(10L)).thenReturn(lipids);

        assertThatThrownBy(() -> measurementOrchestrationService.getLipidsById(jwt, 10L))
                .isInstanceOf(UpstreamErrorException.class);
    }

    @Test
    void deleteLipidsSucceedsWhenOwnedByCurrentPatient() {
        LipidsDto lipids = new LipidsDto();
        lipids.setIdPaciente(1L);
        when(measurementServiceClient.getLipidsById(10L)).thenReturn(lipids);

        measurementOrchestrationService.deleteLipids(jwt, 10L);

        verify(measurementServiceClient).deleteLipids(10L);
    }

    @Test
    void deleteLipidsThrowsWhenOwnedByAnotherPatient() {
        LipidsDto lipids = new LipidsDto();
        lipids.setIdPaciente(99L);
        when(measurementServiceClient.getLipidsById(10L)).thenReturn(lipids);

        assertThatThrownBy(() -> measurementOrchestrationService.deleteLipids(jwt, 10L))
                .isInstanceOf(UpstreamErrorException.class);
    }

    @Test
    void getVitalsByIdSucceedsWhenOwnedByCurrentPatient() {
        VitalsDto vitals = new VitalsDto();
        vitals.setIdPaciente(1L);
        when(measurementServiceClient.getVitalsById(10L)).thenReturn(vitals);

        assertThat(measurementOrchestrationService.getVitalsById(jwt, 10L)).isEqualTo(vitals);
    }

    @Test
    void getVitalsByIdThrowsWhenOwnedByAnotherPatient() {
        VitalsDto vitals = new VitalsDto();
        vitals.setIdPaciente(99L);
        when(measurementServiceClient.getVitalsById(10L)).thenReturn(vitals);

        assertThatThrownBy(() -> measurementOrchestrationService.getVitalsById(jwt, 10L))
                .isInstanceOf(UpstreamErrorException.class);
    }

    @Test
    void deleteVitalsSucceedsWhenOwnedByCurrentPatient() {
        VitalsDto vitals = new VitalsDto();
        vitals.setIdPaciente(1L);
        when(measurementServiceClient.getVitalsById(10L)).thenReturn(vitals);

        measurementOrchestrationService.deleteVitals(jwt, 10L);

        verify(measurementServiceClient).deleteVitals(10L);
    }

    @Test
    void deleteVitalsThrowsWhenOwnedByAnotherPatient() {
        VitalsDto vitals = new VitalsDto();
        vitals.setIdPaciente(99L);
        when(measurementServiceClient.getVitalsById(10L)).thenReturn(vitals);

        assertThatThrownBy(() -> measurementOrchestrationService.deleteVitals(jwt, 10L))
                .isInstanceOf(UpstreamErrorException.class);
    }
}
