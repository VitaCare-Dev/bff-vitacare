package com.grupo10.bff_vitacare.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.grupo10.bff_vitacare.dto.RegisterPatientRequestDto;
import com.grupo10.bff_vitacare.dto.RegisterPatientResponseDto;
import com.grupo10.bff_vitacare.service.PatientRegistrationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;

@ExtendWith(MockitoExtension.class)
class RegistrationControllerTest {

    @Mock
    private PatientRegistrationService patientRegistrationService;

    @InjectMocks
    private RegistrationController registrationController;

    @Test
    void registerReturns201WithTheCombinedResponse() {
        Jwt jwt = Jwt.withTokenValue("token").header("alg", "none").claim("sub", "uid-1").build();
        RegisterPatientRequestDto request = new RegisterPatientRequestDto();
        RegisterPatientResponseDto response = new RegisterPatientResponseDto();
        response.setIdPaciente(1L);
        when(patientRegistrationService.registerPatient(jwt, request)).thenReturn(response);

        ResponseEntity<RegisterPatientResponseDto> result = registrationController.register(jwt, request);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(result.getBody().getIdPaciente()).isEqualTo(1L);
    }
}
