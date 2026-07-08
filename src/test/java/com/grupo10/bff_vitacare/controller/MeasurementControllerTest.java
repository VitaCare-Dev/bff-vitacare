package com.grupo10.bff_vitacare.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.grupo10.bff_vitacare.dto.GlucoseDto;
import com.grupo10.bff_vitacare.dto.GlucoseRequestDto;
import com.grupo10.bff_vitacare.dto.HealthControlDto;
import com.grupo10.bff_vitacare.dto.LipidsDto;
import com.grupo10.bff_vitacare.dto.LipidsRequestDto;
import com.grupo10.bff_vitacare.dto.PageResponseDto;
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
    void listGlucoseReturnsThePage() {
        PageResponseDto<GlucoseDto> page = new PageResponseDto<>(List.of(new GlucoseDto()), 0, 10, 1, 1);
        when(measurementOrchestrationService.listGlucose(jwt, 0, 10, null, null)).thenReturn(page);

        assertThat(measurementController.listGlucose(jwt, 0, 10, null, null).getBody().getContent()).hasSize(1);
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
    void listLipidsReturnsThePage() {
        PageResponseDto<LipidsDto> page = new PageResponseDto<>(List.of(new LipidsDto()), 0, 10, 1, 1);
        when(measurementOrchestrationService.listLipids(jwt, 0, 10, null, null)).thenReturn(page);

        assertThat(measurementController.listLipids(jwt, 0, 10, null, null).getBody().getContent()).hasSize(1);
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
    void listVitalsReturnsThePage() {
        PageResponseDto<VitalsDto> page = new PageResponseDto<>(List.of(new VitalsDto()), 0, 10, 1, 1);
        when(measurementOrchestrationService.listVitals(jwt, 0, 10, null, null)).thenReturn(page);

        assertThat(measurementController.listVitals(jwt, 0, 10, null, null).getBody().getContent()).hasSize(1);
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
