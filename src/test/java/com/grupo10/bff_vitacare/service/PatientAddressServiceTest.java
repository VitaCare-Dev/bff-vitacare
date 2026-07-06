package com.grupo10.bff_vitacare.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.grupo10.bff_vitacare.client.PatientServiceClient;
import com.grupo10.bff_vitacare.dto.AddressDto;
import com.grupo10.bff_vitacare.dto.AddressRequestDto;
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
class PatientAddressServiceTest {

    @Mock
    private PatientContextService patientContextService;

    @Mock
    private PatientServiceClient patientServiceClient;

    @InjectMocks
    private PatientAddressService patientAddressService;

    private Jwt jwt;

    @BeforeEach
    void setUp() {
        jwt = Jwt.withTokenValue("token").header("alg", "none").claim("sub", "uid-1").build();
        PatientDto patient = new PatientDto();
        patient.setIdPaciente(1L);
        lenient().when(patientContextService.resolveCurrentPatient(jwt)).thenReturn(patient);
    }

    @Test
    void createAddressDelegatesToTheClientWithTheResolvedPatientId() {
        AddressRequestDto request = new AddressRequestDto();
        AddressDto created = new AddressDto();
        created.setIdDireccion(5L);
        when(patientServiceClient.createAddress(1L, request)).thenReturn(created);

        AddressDto result = patientAddressService.createAddress(jwt, request);

        assertThat(result.getIdDireccion()).isEqualTo(5L);
    }

    @Test
    void listAddressesDelegatesToTheClient() {
        when(patientServiceClient.listAddressesByPatient(1L)).thenReturn(List.of(new AddressDto()));

        List<AddressDto> result = patientAddressService.listAddresses(jwt);

        assertThat(result).hasSize(1);
    }

    @Test
    void updateAddressSucceedsWhenAddressBelongsToTheCurrentPatient() {
        AddressDto existing = new AddressDto();
        existing.setIdPaciente(1L);
        when(patientServiceClient.getAddressById(5L)).thenReturn(existing);

        AddressRequestDto request = new AddressRequestDto();
        AddressDto updated = new AddressDto();
        updated.setIdDireccion(5L);
        when(patientServiceClient.updateAddress(5L, 1L, request)).thenReturn(updated);

        AddressDto result = patientAddressService.updateAddress(jwt, 5L, request);

        assertThat(result.getIdDireccion()).isEqualTo(5L);
    }

    @Test
    void updateAddressThrowsWhenAddressBelongsToAnotherPatient() {
        AddressDto existing = new AddressDto();
        existing.setIdPaciente(99L);
        when(patientServiceClient.getAddressById(5L)).thenReturn(existing);

        assertThatThrownBy(() -> patientAddressService.updateAddress(jwt, 5L, new AddressRequestDto()))
                .isInstanceOf(UpstreamErrorException.class);
    }

    @Test
    void deleteAddressSucceedsWhenAddressBelongsToTheCurrentPatient() {
        AddressDto existing = new AddressDto();
        existing.setIdPaciente(1L);
        when(patientServiceClient.getAddressById(5L)).thenReturn(existing);

        patientAddressService.deleteAddress(jwt, 5L);

        verify(patientServiceClient).deleteAddress(5L);
    }

    @Test
    void deleteAddressThrowsWhenAddressBelongsToAnotherPatient() {
        AddressDto existing = new AddressDto();
        existing.setIdPaciente(99L);
        when(patientServiceClient.getAddressById(5L)).thenReturn(existing);

        assertThatThrownBy(() -> patientAddressService.deleteAddress(jwt, 5L))
                .isInstanceOf(UpstreamErrorException.class);
    }
}
