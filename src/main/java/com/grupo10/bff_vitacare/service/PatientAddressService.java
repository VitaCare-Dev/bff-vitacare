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

    /**
     * @param patientContextService servicio que resuelve el paciente a partir del token
     * @param patientServiceClient  cliente hacia {@code patient-service}
     */
    public PatientAddressService(PatientContextService patientContextService, PatientServiceClient patientServiceClient) {
        this.patientContextService = patientContextService;
        this.patientServiceClient = patientServiceClient;
    }

    /**
     * Crea una dirección para el paciente autenticado.
     *
     * @param jwt     ID Token de Firebase ya validado
     * @param request datos de la dirección
     * @return la dirección creada
     */
    public AddressDto createAddress(Jwt jwt, AddressRequestDto request) {
        Long idPaciente = resolvePatientId(jwt);
        return patientServiceClient.createAddress(idPaciente, request);
    }

    /**
     * Lista las direcciones del paciente autenticado.
     *
     * @param jwt ID Token de Firebase ya validado
     * @return las direcciones del paciente
     */
    public List<AddressDto> listAddresses(Jwt jwt) {
        return patientServiceClient.listAddressesByPatient(resolvePatientId(jwt));
    }

    /**
     * Actualiza una dirección del paciente autenticado, verificando antes que
     * le pertenezca.
     *
     * @param jwt         ID Token de Firebase ya validado
     * @param idDireccion identificador de la dirección
     * @param request     campos a actualizar
     * @return la dirección actualizada
     * @throws UpstreamErrorException si la dirección no existe o no pertenece al paciente autenticado
     */
    public AddressDto updateAddress(Jwt jwt, Long idDireccion, AddressRequestDto request) {
        Long idPaciente = assertOwnedByCurrentPatient(jwt, idDireccion);
        return patientServiceClient.updateAddress(idDireccion, idPaciente, request);
    }

    /**
     * Elimina una dirección del paciente autenticado, verificando antes que le pertenezca.
     *
     * @param jwt         ID Token de Firebase ya validado
     * @param idDireccion identificador de la dirección a eliminar
     * @throws UpstreamErrorException si la dirección no existe o no pertenece al paciente autenticado
     */
    public void deleteAddress(Jwt jwt, Long idDireccion) {
        assertOwnedByCurrentPatient(jwt, idDireccion);
        patientServiceClient.deleteAddress(idDireccion);
    }

    /**
     * Verifica que una dirección pertenezca al paciente autenticado, ya que
     * {@code patient-service} no valida esa propiedad por su cuenta.
     *
     * @param jwt         ID Token de Firebase ya validado
     * @param idDireccion identificador de la dirección a verificar
     * @return el identificador del paciente autenticado, si la dirección le pertenece
     * @throws UpstreamErrorException si la dirección no existe o pertenece a otro paciente
     */
    private Long assertOwnedByCurrentPatient(Jwt jwt, Long idDireccion) {
        Long idPaciente = resolvePatientId(jwt);
        AddressDto address = patientServiceClient.getAddressById(idDireccion);
        if (!idPaciente.equals(address.getIdPaciente())) {
            throw new UpstreamErrorException(HttpStatus.NOT_FOUND, "Dirección no encontrada");
        }
        return idPaciente;
    }

    /**
     * Resuelve el identificador de paciente asociado al token autenticado.
     *
     * @param jwt ID Token de Firebase ya validado
     * @return el identificador del paciente
     */
    private Long resolvePatientId(Jwt jwt) {
        PatientDto patient = patientContextService.resolveCurrentPatient(jwt);
        return patient.getIdPaciente();
    }

}
