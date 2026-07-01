package com.grupo10.bff_vitacare.service;

import java.util.List;

import com.grupo10.bff_vitacare.client.PatientServiceClient;
import com.grupo10.bff_vitacare.dto.AddressDto;
import com.grupo10.bff_vitacare.dto.AddressRequestDto;
import com.grupo10.bff_vitacare.dto.PatientDto;
import com.grupo10.bff_vitacare.exception.UpstreamErrorException;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

/**
 * Orquesta la gestión de direcciones del paciente autenticado.
 *
 * <p>{@code patient-service} no valida que una dirección pertenezca a quien
 * hace la llamada, así que las operaciones sobre una dirección existente
 * verifican la propiedad aquí antes de reenviar la llamada.</p>
 */
@Service
public class PatientAddressService {

    private final PatientContextService patientContextService;
    private final PatientServiceClient patientServiceClient;

    public PatientAddressService(PatientContextService patientContextService, PatientServiceClient patientServiceClient) {
        this.patientContextService = patientContextService;
        this.patientServiceClient = patientServiceClient;
    }

    public AddressDto createAddress(Jwt jwt, AddressRequestDto request) {
        Long idPaciente = resolvePatientId(jwt);
        return patientServiceClient.createAddress(idPaciente, request);
    }

    public List<AddressDto> listAddresses(Jwt jwt) {
        return patientServiceClient.listAddressesByPatient(resolvePatientId(jwt));
    }

    public AddressDto updateAddress(Jwt jwt, Long idDireccion, AddressRequestDto request) {
        Long idPaciente = assertOwnedByCurrentPatient(jwt, idDireccion);
        return patientServiceClient.updateAddress(idDireccion, idPaciente, request);
    }

    public void deleteAddress(Jwt jwt, Long idDireccion) {
        assertOwnedByCurrentPatient(jwt, idDireccion);
        patientServiceClient.deleteAddress(idDireccion);
    }

    private Long assertOwnedByCurrentPatient(Jwt jwt, Long idDireccion) {
        Long idPaciente = resolvePatientId(jwt);
        AddressDto address = patientServiceClient.getAddressById(idDireccion);
        if (!idPaciente.equals(address.getIdPaciente())) {
            throw new UpstreamErrorException(HttpStatus.NOT_FOUND, "Dirección no encontrada");
        }
        return idPaciente;
    }

    private Long resolvePatientId(Jwt jwt) {
        PatientDto patient = patientContextService.resolveCurrentPatient(jwt);
        return patient.getIdPaciente();
    }

}
