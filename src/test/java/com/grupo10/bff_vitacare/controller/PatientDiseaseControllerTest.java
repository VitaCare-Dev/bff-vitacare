package com.grupo10.bff_vitacare.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import com.grupo10.bff_vitacare.dto.RegisterDiseaseRequestDto;
import com.grupo10.bff_vitacare.service.PatientDiseaseService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;

@ExtendWith(MockitoExtension.class)
class PatientDiseaseControllerTest {

    @Mock
    private PatientDiseaseService patientDiseaseService;

    @InjectMocks
    private PatientDiseaseController patientDiseaseController;

    @Test
    void registerDiseaseReturns201() {
        Jwt jwt = Jwt.withTokenValue("token").header("alg", "none").claim("sub", "uid-1").build();
        RegisterDiseaseRequestDto request = new RegisterDiseaseRequestDto();
        request.setIdEnfermedad(1L);

        ResponseEntity<Void> response = patientDiseaseController.registerDisease(jwt, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        verify(patientDiseaseService).registerDiseaseForCurrentUser(jwt, request);
    }
}
