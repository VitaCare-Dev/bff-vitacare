package com.grupo10.bff_vitacare.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.grupo10.bff_vitacare.dto.GlucoseDto;
import com.grupo10.bff_vitacare.dto.GlucoseRequestDto;
import com.grupo10.bff_vitacare.dto.HealthControlDto;
import com.grupo10.bff_vitacare.dto.LipidsDto;
import com.grupo10.bff_vitacare.dto.LipidsRequestDto;
import com.grupo10.bff_vitacare.dto.VitalsDto;
import com.grupo10.bff_vitacare.dto.VitalsRequestDto;
import com.grupo10.bff_vitacare.service.MeasurementOrchestrationService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;

@ExtendWith(MockitoExtension.class)
class MeasurementControllerTest {

    @Mock
    private MeasurementOrchestrationService measurementOrchestrationService;

    @InjectMocks
    private MeasurementController measurementController;

    private final Jwt jwt = Jwt.withTokenValue("token").header("alg", "none").claim("sub", "uid-1").build();

    @Test
    void createGlucoseReturns201() {
        GlucoseRequestDto request = new GlucoseRequestDto();
        GlucoseDto created = new GlucoseDto();
        created.setIdControl(1L);
        when(measurementOrchestrationService.createGlucose(jwt, request)).thenReturn(created);

        ResponseEntity<GlucoseDto> response = measurementController.createGlucose(jwt, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().getIdControl()).isEqualTo(1L);
    }

    @Test
    void listGlucoseReturnsTheList() {
        when(measurementOrchestrationService.listGlucose(jwt)).thenReturn(List.of(new GlucoseDto()));

        assertThat(measurementController.listGlucose(jwt).getBody()).hasSize(1);
    }

    @Test
    void getLatestGlucoseReturnsTheLatest() {
        GlucoseDto latest = new GlucoseDto();
        latest.setGlucosa(98);
        when(measurementOrchestrationService.getLatestGlucose(jwt)).thenReturn(latest);

        assertThat(measurementController.getLatestGlucose(jwt).getBody().getGlucosa()).isEqualTo(98);
    }

    @Test
    void getGlucoseByIdReturnsTheMeasurement() {
        GlucoseDto glucose = new GlucoseDto();
        glucose.setIdControl(1L);
        when(measurementOrchestrationService.getGlucoseById(jwt, 1L)).thenReturn(glucose);

        assertThat(measurementController.getGlucoseById(jwt, 1L).getBody().getIdControl()).isEqualTo(1L);
    }

    @Test
    void deleteGlucoseReturns204() {
        ResponseEntity<Void> response = measurementController.deleteGlucose(jwt, 1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(measurementOrchestrationService).deleteGlucose(jwt, 1L);
    }

    @Test
    void createLipidsReturns201() {
        LipidsRequestDto request = new LipidsRequestDto();
        LipidsDto created = new LipidsDto();
        created.setIdControl(1L);
        when(measurementOrchestrationService.createLipids(jwt, request)).thenReturn(created);

        ResponseEntity<LipidsDto> response = measurementController.createLipids(jwt, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    void listLipidsReturnsTheList() {
        when(measurementOrchestrationService.listLipids(jwt)).thenReturn(List.of(new LipidsDto()));

        assertThat(measurementController.listLipids(jwt).getBody()).hasSize(1);
    }

    @Test
    void getLatestLipidsReturnsTheLatest() {
        when(measurementOrchestrationService.getLatestLipids(jwt)).thenReturn(new LipidsDto());

        assertThat(measurementController.getLatestLipids(jwt).getBody()).isNotNull();
    }

    @Test
    void getLipidsByIdReturnsTheMeasurement() {
        when(measurementOrchestrationService.getLipidsById(jwt, 1L)).thenReturn(new LipidsDto());

        assertThat(measurementController.getLipidsById(jwt, 1L).getBody()).isNotNull();
    }

    @Test
    void deleteLipidsReturns204() {
        ResponseEntity<Void> response = measurementController.deleteLipids(jwt, 1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(measurementOrchestrationService).deleteLipids(jwt, 1L);
    }

    @Test
    void createVitalsReturns201() {
        VitalsRequestDto request = new VitalsRequestDto();
        VitalsDto created = new VitalsDto();
        created.setIdControl(1L);
        when(measurementOrchestrationService.createVitals(jwt, request)).thenReturn(created);

        ResponseEntity<VitalsDto> response = measurementController.createVitals(jwt, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    void listVitalsReturnsTheList() {
        when(measurementOrchestrationService.listVitals(jwt)).thenReturn(List.of(new VitalsDto()));

        assertThat(measurementController.listVitals(jwt).getBody()).hasSize(1);
    }

    @Test
    void getLatestVitalsReturnsTheLatest() {
        when(measurementOrchestrationService.getLatestVitals(jwt)).thenReturn(new VitalsDto());

        assertThat(measurementController.getLatestVitals(jwt).getBody()).isNotNull();
    }

    @Test
    void getVitalsByIdReturnsTheMeasurement() {
        when(measurementOrchestrationService.getVitalsById(jwt, 1L)).thenReturn(new VitalsDto());

        assertThat(measurementController.getVitalsById(jwt, 1L).getBody()).isNotNull();
    }

    @Test
    void deleteVitalsReturns204() {
        ResponseEntity<Void> response = measurementController.deleteVitals(jwt, 1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(measurementOrchestrationService).deleteVitals(jwt, 1L);
    }

    @Test
    void getHistoryReturnsTheHealthHistory() {
        when(measurementOrchestrationService.getHealthHistory(jwt)).thenReturn(List.of(new HealthControlDto()));

        assertThat(measurementController.getHistory(jwt).getBody()).hasSize(1);
    }
}
