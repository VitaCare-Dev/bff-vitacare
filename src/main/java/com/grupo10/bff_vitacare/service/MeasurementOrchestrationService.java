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

    /**
     * @param patientContextService     servicio que resuelve el paciente a partir del token
     * @param measurementServiceClient  cliente hacia {@code measurement-service}
     */
    public MeasurementOrchestrationService(PatientContextService patientContextService,
                                            MeasurementServiceClient measurementServiceClient) {
        this.patientContextService = patientContextService;
        this.measurementServiceClient = measurementServiceClient;
    }

    /**
     * Registra una medición de glucosa para el paciente autenticado.
     *
     * @param jwt     ID Token de Firebase ya validado
     * @param request datos de la medición
     * @return la medición creada
     */
    public GlucoseDto createGlucose(Jwt jwt, GlucoseRequestDto request) {
        Long idPaciente = resolvePatientId(jwt);
        return measurementServiceClient.createGlucose(idPaciente, request);
    }

    /**
     * Lista el historial de mediciones de glucosa del paciente autenticado.
     *
     * @param jwt ID Token de Firebase ya validado
     * @return las mediciones de glucosa del paciente
     */
    public List<GlucoseDto> listGlucose(Jwt jwt) {
        return measurementServiceClient.listGlucoseByPatient(resolvePatientId(jwt));
    }

    /**
     * Obtiene la medición de glucosa más reciente del paciente autenticado.
     *
     * @param jwt ID Token de Firebase ya validado
     * @return la última medición de glucosa
     */
    public GlucoseDto getLatestGlucose(Jwt jwt) {
        return measurementServiceClient.getLatestGlucose(resolvePatientId(jwt));
    }

    /**
     * Registra un perfil lipídico para el paciente autenticado.
     *
     * @param jwt     ID Token de Firebase ya validado
     * @param request datos del perfil lipídico
     * @return el perfil lipídico creado
     */
    public LipidsDto createLipids(Jwt jwt, LipidsRequestDto request) {
        Long idPaciente = resolvePatientId(jwt);
        return measurementServiceClient.createLipids(idPaciente, request);
    }

    /**
     * Lista el historial de perfiles lipídicos del paciente autenticado.
     *
     * @param jwt ID Token de Firebase ya validado
     * @return los perfiles lipídicos del paciente
     */
    public List<LipidsDto> listLipids(Jwt jwt) {
        return measurementServiceClient.listLipidsByPatient(resolvePatientId(jwt));
    }

    /**
     * Obtiene el perfil lipídico más reciente del paciente autenticado.
     *
     * @param jwt ID Token de Firebase ya validado
     * @return el último perfil lipídico
     */
    public LipidsDto getLatestLipids(Jwt jwt) {
        return measurementServiceClient.getLatestLipids(resolvePatientId(jwt));
    }

    /**
     * Registra signos vitales para el paciente autenticado.
     *
     * @param jwt     ID Token de Firebase ya validado
     * @param request datos de los signos vitales
     * @return la medición creada
     */
    public VitalsDto createVitals(Jwt jwt, VitalsRequestDto request) {
        Long idPaciente = resolvePatientId(jwt);
        return measurementServiceClient.createVitals(idPaciente, request);
    }

    /**
     * Lista el historial de signos vitales del paciente autenticado.
     *
     * @param jwt ID Token de Firebase ya validado
     * @return las mediciones de signos vitales del paciente
     */
    public List<VitalsDto> listVitals(Jwt jwt) {
        return measurementServiceClient.listVitalsByPatient(resolvePatientId(jwt));
    }

    /**
     * Obtiene la medición de signos vitales más reciente del paciente autenticado.
     *
     * @param jwt ID Token de Firebase ya validado
     * @return la última medición de signos vitales
     */
    public VitalsDto getLatestVitals(Jwt jwt) {
        return measurementServiceClient.getLatestVitals(resolvePatientId(jwt));
    }

    /**
     * Lista el historial combinado de controles de salud del paciente autenticado.
     *
     * @param jwt ID Token de Firebase ya validado
     * @return el historial de controles del paciente
     */
    public List<HealthControlDto> getHealthHistory(Jwt jwt) {
        return measurementServiceClient.getHealthHistory(resolvePatientId(jwt));
    }

    /**
     * Busca una medición de glucosa, verificando que pertenezca al paciente autenticado.
     *
     * @param jwt       ID Token de Firebase ya validado
     * @param idControl identificador del control
     * @return la medición de glucosa encontrada
     * @throws UpstreamErrorException si no existe o pertenece a otro paciente
     */
    public GlucoseDto getGlucoseById(Jwt jwt, Long idControl) {
        GlucoseDto glucose = measurementServiceClient.getGlucoseById(idControl);
        assertOwned(jwt, glucose.getIdPaciente());
        return glucose;
    }

    /**
     * Elimina una medición de glucosa, verificando antes que pertenezca al paciente autenticado.
     *
     * @param jwt       ID Token de Firebase ya validado
     * @param idControl identificador del control a eliminar
     * @throws UpstreamErrorException si no existe o pertenece a otro paciente
     */
    public void deleteGlucose(Jwt jwt, Long idControl) {
        GlucoseDto glucose = measurementServiceClient.getGlucoseById(idControl);
        assertOwned(jwt, glucose.getIdPaciente());
        measurementServiceClient.deleteGlucose(idControl);
    }

    /**
     * Busca un perfil lipídico, verificando que pertenezca al paciente autenticado.
     *
     * @param jwt       ID Token de Firebase ya validado
     * @param idControl identificador del control
     * @return el perfil lipídico encontrado
     * @throws UpstreamErrorException si no existe o pertenece a otro paciente
     */
    public LipidsDto getLipidsById(Jwt jwt, Long idControl) {
        LipidsDto lipids = measurementServiceClient.getLipidsById(idControl);
        assertOwned(jwt, lipids.getIdPaciente());
        return lipids;
    }

    /**
     * Elimina un perfil lipídico, verificando antes que pertenezca al paciente autenticado.
     *
     * @param jwt       ID Token de Firebase ya validado
     * @param idControl identificador del control a eliminar
     * @throws UpstreamErrorException si no existe o pertenece a otro paciente
     */
    public void deleteLipids(Jwt jwt, Long idControl) {
        LipidsDto lipids = measurementServiceClient.getLipidsById(idControl);
        assertOwned(jwt, lipids.getIdPaciente());
        measurementServiceClient.deleteLipids(idControl);
    }

    /**
     * Busca una medición de signos vitales, verificando que pertenezca al paciente autenticado.
     *
     * @param jwt       ID Token de Firebase ya validado
     * @param idControl identificador del control
     * @return la medición de signos vitales encontrada
     * @throws UpstreamErrorException si no existe o pertenece a otro paciente
     */
    public VitalsDto getVitalsById(Jwt jwt, Long idControl) {
        VitalsDto vitals = measurementServiceClient.getVitalsById(idControl);
        assertOwned(jwt, vitals.getIdPaciente());
        return vitals;
    }

    /**
     * Elimina una medición de signos vitales, verificando antes que pertenezca al paciente autenticado.
     *
     * @param jwt       ID Token de Firebase ya validado
     * @param idControl identificador del control a eliminar
     * @throws UpstreamErrorException si no existe o pertenece a otro paciente
     */
    public void deleteVitals(Jwt jwt, Long idControl) {
        VitalsDto vitals = measurementServiceClient.getVitalsById(idControl);
        assertOwned(jwt, vitals.getIdPaciente());
        measurementServiceClient.deleteVitals(idControl);
    }

    /**
     * Verifica que una medición pertenezca al paciente autenticado.
     *
     * @param jwt                 ID Token de Firebase ya validado
     * @param idPacienteMedicion  identificador de paciente dueño de la medición
     * @throws UpstreamErrorException si la medición pertenece a otro paciente
     */
    private void assertOwned(Jwt jwt, Long idPacienteMedicion) {
        if (!resolvePatientId(jwt).equals(idPacienteMedicion)) {
            throw new UpstreamErrorException(HttpStatus.NOT_FOUND, "Medición no encontrada");
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
