package com.grupo10.bff_vitacare.service;

import java.util.List;

import com.grupo10.bff_vitacare.client.MedicationServiceClient;
import com.grupo10.bff_vitacare.dto.MedicationDto;
import com.grupo10.bff_vitacare.dto.MedicationRequestDto;
import com.grupo10.bff_vitacare.dto.PatientDto;
import com.grupo10.bff_vitacare.exception.UpstreamErrorException;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

/**
 * Orquesta la gestión de medicamentos del paciente autenticado.
 *
 * <p>{@code medication-service} no valida que un {@code idMedicamento}
 * pertenezca a quien hace la llamada, así que las operaciones que actúan
 * sobre un medicamento existente ({@link #deactivateMedication} y
 * {@link #deleteMedication}) verifican la propiedad aquí antes de reenviar
 * la llamada.</p>
 */
@Service
public class MedicationOrchestrationService {

    private final PatientContextService patientContextService;
    private final MedicationServiceClient medicationServiceClient;

    /**
     * @param patientContextService    servicio que resuelve el paciente a partir del token
     * @param medicationServiceClient  cliente hacia {@code medication-service}
     */
    public MedicationOrchestrationService(PatientContextService patientContextService,
                                           MedicationServiceClient medicationServiceClient) {
        this.patientContextService = patientContextService;
        this.medicationServiceClient = medicationServiceClient;
    }

    /**
     * Registra un medicamento para el paciente autenticado.
     *
     * @param jwt     ID Token de Firebase ya validado
     * @param request datos del medicamento
     * @return el medicamento creado
     */
    public MedicationDto createMedication(Jwt jwt, MedicationRequestDto request) {
        Long idPaciente = resolvePatientId(jwt);
        return medicationServiceClient.createMedication(idPaciente, request);
    }

    /**
     * Lista los medicamentos del paciente autenticado.
     *
     * @param jwt        ID Token de Firebase ya validado
     * @param onlyActive si es {@code true}, solo devuelve los medicamentos activos
     * @return los medicamentos del paciente
     */
    public List<MedicationDto> listMedications(Jwt jwt, boolean onlyActive) {
        Long idPaciente = resolvePatientId(jwt);
        return onlyActive
                ? medicationServiceClient.listActiveByPatient(idPaciente)
                : medicationServiceClient.listByPatient(idPaciente);
    }

    /**
     * Desactiva un medicamento del paciente autenticado, verificando antes que le pertenezca.
     *
     * @param jwt           ID Token de Firebase ya validado
     * @param idMedicamento identificador del medicamento
     * @return el medicamento ya desactivado
     * @throws UpstreamErrorException si el medicamento no existe o pertenece a otro paciente
     */
    public MedicationDto deactivateMedication(Jwt jwt, Long idMedicamento) {
        assertOwnedByCurrentPatient(jwt, idMedicamento);
        return medicationServiceClient.deactivate(idMedicamento);
    }

    /**
     * Elimina un medicamento del paciente autenticado, verificando antes que le pertenezca.
     *
     * @param jwt           ID Token de Firebase ya validado
     * @param idMedicamento identificador del medicamento a eliminar
     * @throws UpstreamErrorException si el medicamento no existe o pertenece a otro paciente
     */
    public void deleteMedication(Jwt jwt, Long idMedicamento) {
        assertOwnedByCurrentPatient(jwt, idMedicamento);
        medicationServiceClient.delete(idMedicamento);
    }

    /**
     * Verifica que un medicamento pertenezca al paciente autenticado.
     *
     * @param jwt           ID Token de Firebase ya validado
     * @param idMedicamento identificador del medicamento a verificar
     * @throws UpstreamErrorException si el medicamento no existe o pertenece a otro paciente
     */
    private void assertOwnedByCurrentPatient(Jwt jwt, Long idMedicamento) {
        Long idPaciente = resolvePatientId(jwt);
        MedicationDto medication = medicationServiceClient.getById(idMedicamento);
        if (!idPaciente.equals(medication.getIdPaciente())) {
            throw new UpstreamErrorException(HttpStatus.NOT_FOUND, "Medicamento no encontrado");
        }
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
