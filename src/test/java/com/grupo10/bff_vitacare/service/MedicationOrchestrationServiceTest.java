package com.grupo10.bff_vitacare.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.grupo10.bff_vitacare.client.MedicationServiceClient;
import com.grupo10.bff_vitacare.dto.MedicationDto;
import com.grupo10.bff_vitacare.dto.MedicationRequestDto;
import com.grupo10.bff_vitacare.dto.PatientDto;
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
class MedicationOrchestrationServiceTest {

    @Mock
    private PatientContextService patientContextService;

    @Mock
    private MedicationServiceClient medicationServiceClient;

    @InjectMocks
    private MedicationOrchestrationService medicationOrchestrationService;

    private Jwt jwt;

    @BeforeEach
    void setUp() {
        jwt = Jwt.withTokenValue("token").header("alg", "none").claim("sub", "uid-1").build();
        PatientDto patient = new PatientDto();
        patient.setIdPaciente(1L);
        lenient().when(patientContextService.resolveCurrentPatient(jwt)).thenReturn(patient);
    }

    @Test
    void createMedicationDelegatesWithResolvedPatientId() {
        MedicationRequestDto request = new MedicationRequestDto();
        MedicationDto created = new MedicationDto();
        created.setIdMedicamento(1L);
        when(medicationServiceClient.createMedication(1L, request)).thenReturn(created);

        assertThat(medicationOrchestrationService.createMedication(jwt, request).getIdMedicamento()).isEqualTo(1L);
    }

    @Test
    void listMedicationsReturnsAllWhenNotOnlyActive() {
        when(medicationServiceClient.listByPatient(1L)).thenReturn(List.of(new MedicationDto(), new MedicationDto()));

        assertThat(medicationOrchestrationService.listMedications(jwt, false)).hasSize(2);
    }

    @Test
    void listMedicationsReturnsOnlyActiveWhenRequested() {
        when(medicationServiceClient.listActiveByPatient(1L)).thenReturn(List.of(new MedicationDto()));

        assertThat(medicationOrchestrationService.listMedications(jwt, true)).hasSize(1);
    }

    @Test
    void deactivateMedicationSucceedsWhenOwnedByCurrentPatient() {
        MedicationDto medication = new MedicationDto();
        medication.setIdPaciente(1L);
        when(medicationServiceClient.getById(10L)).thenReturn(medication);

        MedicationDto deactivated = new MedicationDto();
        deactivated.setActivo(0);
        when(medicationServiceClient.deactivate(10L)).thenReturn(deactivated);

        assertThat(medicationOrchestrationService.deactivateMedication(jwt, 10L).getActivo()).isEqualTo(0);
    }

    @Test
    void deactivateMedicationThrowsWhenOwnedByAnotherPatient() {
        MedicationDto medication = new MedicationDto();
        medication.setIdPaciente(99L);
        when(medicationServiceClient.getById(10L)).thenReturn(medication);

        assertThatThrownBy(() -> medicationOrchestrationService.deactivateMedication(jwt, 10L))
                .isInstanceOf(UpstreamErrorException.class);
    }

    @Test
    void deleteMedicationSucceedsWhenOwnedByCurrentPatient() {
        MedicationDto medication = new MedicationDto();
        medication.setIdPaciente(1L);
        when(medicationServiceClient.getById(10L)).thenReturn(medication);

        medicationOrchestrationService.deleteMedication(jwt, 10L);

        verify(medicationServiceClient).delete(10L);
    }

    @Test
    void deleteMedicationThrowsWhenOwnedByAnotherPatient() {
        MedicationDto medication = new MedicationDto();
        medication.setIdPaciente(99L);
        when(medicationServiceClient.getById(10L)).thenReturn(medication);

        assertThatThrownBy(() -> medicationOrchestrationService.deleteMedication(jwt, 10L))
                .isInstanceOf(UpstreamErrorException.class);
    }
}
