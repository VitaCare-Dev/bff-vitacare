package com.grupo10.bff_vitacare.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.grupo10.bff_vitacare.dto.AddressDto;
import com.grupo10.bff_vitacare.dto.AddressRequestDto;
import com.grupo10.bff_vitacare.service.PatientAddressService;
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
class PatientAddressControllerTest {

    @Mock
    private PatientAddressService patientAddressService;

    @InjectMocks
    private PatientAddressController patientAddressController;

    private final Jwt jwt = Jwt.withTokenValue("token").header("alg", "none").claim("sub", "uid-1").build();

    @Test
    void createAddressReturns201() {
        AddressRequestDto request = new AddressRequestDto();
        AddressDto created = new AddressDto();
        created.setIdDireccion(1L);
        when(patientAddressService.createAddress(jwt, request)).thenReturn(created);

        ResponseEntity<AddressDto> response = patientAddressController.createAddress(jwt, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().getIdDireccion()).isEqualTo(1L);
    }

    @Test
    void listAddressesReturnsTheList() {
        when(patientAddressService.listAddresses(jwt)).thenReturn(List.of(new AddressDto()));

        ResponseEntity<List<AddressDto>> response = patientAddressController.listAddresses(jwt);

        assertThat(response.getBody()).hasSize(1);
    }

    @Test
    void updateAddressReturnsTheUpdatedAddress() {
        AddressRequestDto request = new AddressRequestDto();
        AddressDto updated = new AddressDto();
        updated.setCalle("Nueva calle");
        when(patientAddressService.updateAddress(jwt, 1L, request)).thenReturn(updated);

        ResponseEntity<AddressDto> response = patientAddressController.updateAddress(jwt, 1L, request);

        assertThat(response.getBody().getCalle()).isEqualTo("Nueva calle");
    }

    @Test
    void deleteAddressReturns204() {
        ResponseEntity<Void> response = patientAddressController.deleteAddress(jwt, 1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(patientAddressService).deleteAddress(jwt, 1L);
    }
}
