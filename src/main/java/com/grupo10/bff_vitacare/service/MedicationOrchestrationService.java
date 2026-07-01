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

    public MedicationOrchestrationService(PatientContextService patientContextService,
                                           MedicationServiceClient medicationServiceClient) {
        this.patientContextService = patientContextService;
        this.medicationServiceClient = medicationServiceClient;
    }

    public MedicationDto createMedication(Jwt jwt, MedicationRequestDto request) {
        Long idPaciente = resolvePatientId(jwt);
        return medicationServiceClient.createMedication(idPaciente, request);
    }

    public List<MedicationDto> listMedications(Jwt jwt, boolean onlyActive) {
        Long idPaciente = resolvePatientId(jwt);
        return onlyActive
                ? medicationServiceClient.listActiveByPatient(idPaciente)
                : medicationServiceClient.listByPatient(idPaciente);
    }

    public MedicationDto deactivateMedication(Jwt jwt, Long idMedicamento) {
        assertOwnedByCurrentPatient(jwt, idMedicamento);
        return medicationServiceClient.deactivate(idMedicamento);
    }

    public void deleteMedication(Jwt jwt, Long idMedicamento) {
        assertOwnedByCurrentPatient(jwt, idMedicamento);
        medicationServiceClient.delete(idMedicamento);
    }

    private void assertOwnedByCurrentPatient(Jwt jwt, Long idMedicamento) {
        Long idPaciente = resolvePatientId(jwt);
        MedicationDto medication = medicationServiceClient.getById(idMedicamento);
        if (!idPaciente.equals(medication.getIdPaciente())) {
            throw new UpstreamErrorException(HttpStatus.NOT_FOUND, "Medicamento no encontrado");
        }
    }

    private Long resolvePatientId(Jwt jwt) {
        PatientDto patient = patientContextService.resolveCurrentPatient(jwt);
        return patient.getIdPaciente();
    }

}
