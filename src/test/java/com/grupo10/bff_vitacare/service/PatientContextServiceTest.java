package com.grupo10.bff_vitacare.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.grupo10.bff_vitacare.client.PatientServiceClient;
import com.grupo10.bff_vitacare.dto.AuthenticatedUserDto;
import com.grupo10.bff_vitacare.dto.PatientDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;

@ExtendWith(MockitoExtension.class)
class PatientContextServiceTest {

    @Mock
    private AuthContextService authContextService;

    @Mock
    private PatientServiceClient patientServiceClient;

    @InjectMocks
    private PatientContextService patientContextService;

    @Test
    void resolveCurrentPatientResolvesTheUserFirstThenThePatient() {
        Jwt jwt = Jwt.withTokenValue("token").header("alg", "none").claim("sub", "uid-1").build();

        AuthenticatedUserDto user = new AuthenticatedUserDto();
        user.setId(1L);
        when(authContextService.resolveCurrentUser(jwt)).thenReturn(user);

        PatientDto patient = new PatientDto();
        patient.setIdPaciente(10L);
        when(patientServiceClient.findByUserId(1L)).thenReturn(patient);

        PatientDto result = patientContextService.resolveCurrentPatient(jwt);

        assertThat(result.getIdPaciente()).isEqualTo(10L);
    }
}
