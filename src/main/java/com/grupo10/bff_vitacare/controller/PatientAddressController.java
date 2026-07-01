package com.grupo10.bff_vitacare.controller;

import java.util.List;

import com.grupo10.bff_vitacare.dto.AddressDto;
import com.grupo10.bff_vitacare.dto.AddressRequestDto;
import com.grupo10.bff_vitacare.service.PatientAddressService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Gestiona las direcciones del paciente autenticado.
 */
@RestController
@RequestMapping("/api/patients/me/addresses")
public class PatientAddressController {

    private final PatientAddressService patientAddressService;

    public PatientAddressController(PatientAddressService patientAddressService) {
        this.patientAddressService = patientAddressService;
    }

    @PostMapping
    public ResponseEntity<AddressDto> createAddress(@AuthenticationPrincipal Jwt jwt,
                                                      @RequestBody AddressRequestDto request) {
        AddressDto response = patientAddressService.createAddress(jwt, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<AddressDto>> listAddresses(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(patientAddressService.listAddresses(jwt));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AddressDto> updateAddress(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id,
                                                      @RequestBody AddressRequestDto request) {
        return ResponseEntity.ok(patientAddressService.updateAddress(jwt, id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAddress(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id) {
        patientAddressService.deleteAddress(jwt, id);
        return ResponseEntity.noContent().build();
    }

}
