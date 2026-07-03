package com.grupo10.bff_vitacare.service;

import java.util.List;

import com.grupo10.bff_vitacare.client.AiAlertServiceClient;
import com.grupo10.bff_vitacare.dto.AlertaDto;
import com.grupo10.bff_vitacare.dto.PatientDto;
import com.grupo10.bff_vitacare.dto.RecomendacionDto;
import com.grupo10.bff_vitacare.exception.UpstreamErrorException;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

/**
 * Orquesta alertas y recomendaciones de IA para el paciente autenticado.
 *
 * <p>Las funciones HTTP de {@code ai-alert-service} no verifican que una
 * alerta o recomendación pertenezca a quien hace la llamada (reciben el
 * {@code idPaciente} directo por la URL), así que las operaciones sobre un
 * id puntual verifican la propiedad aquí antes de reenviar la llamada.</p>
 */
@Service
public class AiAlertOrchestrationService {

    private final PatientContextService patientContextService;
    private final AiAlertServiceClient aiAlertServiceClient;

    public AiAlertOrchestrationService(PatientContextService patientContextService,
                                        AiAlertServiceClient aiAlertServiceClient) {
        this.patientContextService = patientContextService;
        this.aiAlertServiceClient = aiAlertServiceClient;
    }

    public List<AlertaDto> getAlertas(Jwt jwt) {
        return aiAlertServiceClient.listAlertas(resolvePatientId(jwt));
    }

    public List<AlertaDto> getAlertasNoLeidas(Jwt jwt) {
        return aiAlertServiceClient.listAlertasNoLeidas(resolvePatientId(jwt));
    }

    public void marcarAlertaLeida(Jwt jwt, Long idAlerta) {
        assertAlertaOwnedByCurrentPatient(jwt, idAlerta);
        aiAlertServiceClient.marcarAlertaLeida(idAlerta);
    }

    public void marcarTodasAlertasLeidas(Jwt jwt) {
        aiAlertServiceClient.marcarTodasAlertasLeidas(resolvePatientId(jwt));
    }

    public List<RecomendacionDto> getRecomendaciones(Jwt jwt) {
        return aiAlertServiceClient.listRecomendaciones(resolvePatientId(jwt));
    }

    public List<RecomendacionDto> getRecomendacionesNoLeidas(Jwt jwt) {
        return aiAlertServiceClient.listRecomendacionesNoLeidas(resolvePatientId(jwt));
    }

    public void marcarRecomendacionLeida(Jwt jwt, Long idRecomendacion) {
        assertRecomendacionOwnedByCurrentPatient(jwt, idRecomendacion);
        aiAlertServiceClient.marcarRecomendacionLeida(idRecomendacion);
    }

    public void marcarTodasRecomendacionesLeidas(Jwt jwt) {
        aiAlertServiceClient.marcarTodasRecomendacionesLeidas(resolvePatientId(jwt));
    }

    private void assertAlertaOwnedByCurrentPatient(Jwt jwt, Long idAlerta) {
        Long idPaciente = resolvePatientId(jwt);
        boolean owned = aiAlertServiceClient.listAlertas(idPaciente).stream()
                .anyMatch(alerta -> idAlerta.equals(alerta.getIdAlertaIa()));
        if (!owned) {
            throw new UpstreamErrorException(HttpStatus.NOT_FOUND, "Alerta no encontrada");
        }
    }

    private void assertRecomendacionOwnedByCurrentPatient(Jwt jwt, Long idRecomendacion) {
        Long idPaciente = resolvePatientId(jwt);
        boolean owned = aiAlertServiceClient.listRecomendaciones(idPaciente).stream()
                .anyMatch(recomendacion -> idRecomendacion.equals(recomendacion.getIdRecomendacion()));
        if (!owned) {
            throw new UpstreamErrorException(HttpStatus.NOT_FOUND, "Recomendación no encontrada");
        }
    }

    private Long resolvePatientId(Jwt jwt) {
        PatientDto patient = patientContextService.resolveCurrentPatient(jwt);
        return patient.getIdPaciente();
    }

}
