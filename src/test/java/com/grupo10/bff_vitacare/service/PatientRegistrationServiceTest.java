package com.grupo10.bff_vitacare.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.grupo10.bff_vitacare.client.PatientServiceClient;
import com.grupo10.bff_vitacare.client.UserServiceClient;
import com.grupo10.bff_vitacare.dto.AuthenticatedUserDto;
import com.grupo10.bff_vitacare.dto.PatientDto;
import com.grupo10.bff_vitacare.dto.RegisterPatientRequestDto;
import com.grupo10.bff_vitacare.dto.RegisterPatientResponseDto;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;

@ExtendWith(MockitoExtension.class)
class PatientRegistrationServiceTest {

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private PatientServiceClient patientServiceClient;

    @InjectMocks
    private PatientRegistrationService patientRegistrationService;

    private Jwt jwt() {
        return Jwt.withTokenValue("token")
                .header("alg", "none")
                .claim("sub", "uid-1")
                .claim("email", "a@b.cl")
                .build();
    }

    private RegisterPatientRequestDto sampleRequest() {
        RegisterPatientRequestDto request = new RegisterPatientRequestDto();
        request.setRut("12.345.678-9");
        request.setNombre("María");
        request.setApellidoPaterno("Pérez");
        request.setFechaNacimiento(LocalDate.of(1990, 5, 15));
        request.setTelefonoPrincipal("+56912345678");
        return request;
    }

    @Test
    void registerPatientReusesAnExistingSyncedUser() {
        AuthenticatedUserDto user = new AuthenticatedUserDto();
        user.setId(1L);
        user.setCorreo("a@b.cl");
        user.setRol("PACIENTE");
        when(userServiceClient.tryFindByFirebaseUid("uid-1")).thenReturn(Optional.of(user));

        PatientDto patient = new PatientDto();
        patient.setIdPaciente(10L);
        patient.setRut("12.345.678-9");
        when(patientServiceClient.createPatient(1L, sampleRequest())).thenReturn(patient);

        RegisterPatientResponseDto response = patientRegistrationService.registerPatient(jwt(), sampleRequest());

        assertThat(response.getIdUsuario()).isEqualTo(1L);
        assertThat(response.getIdPaciente()).isEqualTo(10L);
        assertThat(response.getRut()).isEqualTo("12.345.678-9");
        verify(userServiceClient, never()).createUser(any(), any(), any());
    }

    @Test
    void registerPatientCreatesTheUserWhenNotSyncedYet() {
        when(userServiceClient.tryFindByFirebaseUid("uid-1")).thenReturn(Optional.empty());

        AuthenticatedUserDto createdUser = new AuthenticatedUserDto();
        createdUser.setId(2L);
        createdUser.setCorreo("a@b.cl");
        when(userServiceClient.createUser("a@b.cl", "uid-1", "PACIENTE")).thenReturn(createdUser);

        PatientDto patient = new PatientDto();
        patient.setIdPaciente(20L);
        when(patientServiceClient.createPatient(2L, sampleRequest())).thenReturn(patient);

        RegisterPatientResponseDto response = patientRegistrationService.registerPatient(jwt(), sampleRequest());

        assertThat(response.getIdUsuario()).isEqualTo(2L);
        assertThat(response.getIdPaciente()).isEqualTo(20L);
    }
}
