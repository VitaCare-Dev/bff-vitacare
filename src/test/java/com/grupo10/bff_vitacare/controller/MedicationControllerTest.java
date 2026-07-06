package com.grupo10.bff_vitacare.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.grupo10.bff_vitacare.dto.MedicationDto;
import com.grupo10.bff_vitacare.dto.MedicationRequestDto;
import com.grupo10.bff_vitacare.service.MedicationOrchestrationService;
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
class MedicationControllerTest {

    @Mock
    private MedicationOrchestrationService medicationOrchestrationService;

    @InjectMocks
    private MedicationController medicationController;

    private final Jwt jwt = Jwt.withTokenValue("token").header("alg", "none").claim("sub", "uid-1").build();

    @Test
    void createMedicationReturns201() {
        MedicationRequestDto request = new MedicationRequestDto();
        MedicationDto created = new MedicationDto();
        created.setIdMedicamento(1L);
        when(medicationOrchestrationService.createMedication(jwt, request)).thenReturn(created);

        ResponseEntity<MedicationDto> response = medicationController.createMedication(jwt, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().getIdMedicamento()).isEqualTo(1L);
    }

    @Test
    void listMedicationsPassesTheActiveFlagThrough() {
        when(medicationOrchestrationService.listMedications(jwt, true)).thenReturn(List.of(new MedicationDto()));

        ResponseEntity<List<MedicationDto>> response = medicationController.listMedications(jwt, true);

        assertThat(response.getBody()).hasSize(1);
    }

    @Test
    void deactivateMedicationReturnsTheDeactivatedMedication() {
        MedicationDto deactivated = new MedicationDto();
        deactivated.setActivo(0);
        when(medicationOrchestrationService.deactivateMedication(jwt, 1L)).thenReturn(deactivated);

        ResponseEntity<MedicationDto> response = medicationController.deactivateMedication(jwt, 1L);

        assertThat(response.getBody().getActivo()).isEqualTo(0);
    }

    @Test
    void deleteMedicationReturns204() {
        ResponseEntity<Void> response = medicationController.deleteMedication(jwt, 1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(medicationOrchestrationService).deleteMedication(jwt, 1L);
    }
}
