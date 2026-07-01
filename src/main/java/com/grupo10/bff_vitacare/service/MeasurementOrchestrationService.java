package com.grupo10.bff_vitacare.service;

import java.util.List;

import com.grupo10.bff_vitacare.client.MeasurementServiceClient;
import com.grupo10.bff_vitacare.dto.GlucoseDto;
import com.grupo10.bff_vitacare.dto.GlucoseRequestDto;
import com.grupo10.bff_vitacare.dto.HealthControlDto;
import com.grupo10.bff_vitacare.dto.LipidsDto;
import com.grupo10.bff_vitacare.dto.LipidsRequestDto;
import com.grupo10.bff_vitacare.dto.PatientDto;
import com.grupo10.bff_vitacare.dto.VitalsDto;
import com.grupo10.bff_vitacare.dto.VitalsRequestDto;
import com.grupo10.bff_vitacare.exception.UpstreamErrorException;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

/**
 * Orquesta el registro y consulta de mediciones de salud del paciente
 * autenticado, resolviendo su {@code idPaciente} antes de llamar a
 * {@code measurement-service}.
 */
@Service
public class MeasurementOrchestrationService {

    private final PatientContextService patientContextService;
    private final MeasurementServiceClient measurementServiceClient;

    public MeasurementOrchestrationService(PatientContextService patientContextService,
                                            MeasurementServiceClient measurementServiceClient) {
        this.patientContextService = patientContextService;
        this.measurementServiceClient = measurementServiceClient;
    }

    public GlucoseDto createGlucose(Jwt jwt, GlucoseRequestDto request) {
        Long idPaciente = resolvePatientId(jwt);
        return measurementServiceClient.createGlucose(idPaciente, request);
    }

    public List<GlucoseDto> listGlucose(Jwt jwt) {
        return measurementServiceClient.listGlucoseByPatient(resolvePatientId(jwt));
    }

    public GlucoseDto getLatestGlucose(Jwt jwt) {
        return measurementServiceClient.getLatestGlucose(resolvePatientId(jwt));
    }

    public LipidsDto createLipids(Jwt jwt, LipidsRequestDto request) {
        Long idPaciente = resolvePatientId(jwt);
        return measurementServiceClient.createLipids(idPaciente, request);
    }

    public List<LipidsDto> listLipids(Jwt jwt) {
        return measurementServiceClient.listLipidsByPatient(resolvePatientId(jwt));
    }

    public LipidsDto getLatestLipids(Jwt jwt) {
        return measurementServiceClient.getLatestLipids(resolvePatientId(jwt));
    }

    public VitalsDto createVitals(Jwt jwt, VitalsRequestDto request) {
        Long idPaciente = resolvePatientId(jwt);
        return measurementServiceClient.createVitals(idPaciente, request);
    }

    public List<VitalsDto> listVitals(Jwt jwt) {
        return measurementServiceClient.listVitalsByPatient(resolvePatientId(jwt));
    }

    public VitalsDto getLatestVitals(Jwt jwt) {
        return measurementServiceClient.getLatestVitals(resolvePatientId(jwt));
    }

    public List<HealthControlDto> getHealthHistory(Jwt jwt) {
        return measurementServiceClient.getHealthHistory(resolvePatientId(jwt));
    }

    public GlucoseDto getGlucoseById(Jwt jwt, Long idControl) {
        GlucoseDto glucose = measurementServiceClient.getGlucoseById(idControl);
        assertOwned(jwt, glucose.getIdPaciente());
        return glucose;
    }

    public void deleteGlucose(Jwt jwt, Long idControl) {
        GlucoseDto glucose = measurementServiceClient.getGlucoseById(idControl);
        assertOwned(jwt, glucose.getIdPaciente());
        measurementServiceClient.deleteGlucose(idControl);
    }

    public LipidsDto getLipidsById(Jwt jwt, Long idControl) {
        LipidsDto lipids = measurementServiceClient.getLipidsById(idControl);
        assertOwned(jwt, lipids.getIdPaciente());
        return lipids;
    }

    public void deleteLipids(Jwt jwt, Long idControl) {
        LipidsDto lipids = measurementServiceClient.getLipidsById(idControl);
        assertOwned(jwt, lipids.getIdPaciente());
        measurementServiceClient.deleteLipids(idControl);
    }

    public VitalsDto getVitalsById(Jwt jwt, Long idControl) {
        VitalsDto vitals = measurementServiceClient.getVitalsById(idControl);
        assertOwned(jwt, vitals.getIdPaciente());
        return vitals;
    }

    public void deleteVitals(Jwt jwt, Long idControl) {
        VitalsDto vitals = measurementServiceClient.getVitalsById(idControl);
        assertOwned(jwt, vitals.getIdPaciente());
        measurementServiceClient.deleteVitals(idControl);
    }

    private void assertOwned(Jwt jwt, Long idPacienteMedicion) {
        if (!resolvePatientId(jwt).equals(idPacienteMedicion)) {
            throw new UpstreamErrorException(HttpStatus.NOT_FOUND, "Medición no encontrada");
        }
    }

    private Long resolvePatientId(Jwt jwt) {
        PatientDto patient = patientContextService.resolveCurrentPatient(jwt);
        return patient.getIdPaciente();
    }

}
