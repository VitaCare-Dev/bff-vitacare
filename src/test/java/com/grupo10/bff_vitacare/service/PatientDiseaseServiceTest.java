package com.grupo10.bff_vitacare.service;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.grupo10.bff_vitacare.client.PatientServiceClient;
import com.grupo10.bff_vitacare.dto.PatientDto;
import com.grupo10.bff_vitacare.dto.RegisterDiseaseRequestDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;

@ExtendWith(MockitoExtension.class)
class PatientDiseaseServiceTest {

    @Mock
    private PatientContextService patientContextService;

    @Mock
    private PatientServiceClient patientServiceClient;

    @InjectMocks
    private PatientDiseaseService patientDiseaseService;

    @Test
    void registerDiseaseForCurrentUserResolvesThePatientAndDelegates() {
        Jwt jwt = Jwt.withTokenValue("token").header("alg", "none").claim("sub", "uid-1").build();
        PatientDto patient = new PatientDto();
        patient.setIdPaciente(1L);
        when(patientContextService.resolveCurrentPatient(jwt)).thenReturn(patient);

        RegisterDiseaseRequestDto request = new RegisterDiseaseRequestDto();
        request.setIdEnfermedad(2L);

        patientDiseaseService.registerDiseaseForCurrentUser(jwt, request);

        verify(patientServiceClient).registerDisease(1L, 2L);
    }
}
