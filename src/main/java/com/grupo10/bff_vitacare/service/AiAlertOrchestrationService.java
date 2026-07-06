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

    /**
     * @param patientContextService servicio que resuelve el paciente a partir del token
     * @param aiAlertServiceClient  cliente hacia {@code ai-alert-service}
     */
    public AiAlertOrchestrationService(PatientContextService patientContextService,
                                        AiAlertServiceClient aiAlertServiceClient) {
        this.patientContextService = patientContextService;
        this.aiAlertServiceClient = aiAlertServiceClient;
    }

    /**
     * Lista todas las alertas de IA del paciente autenticado.
     *
     * @param jwt ID Token de Firebase ya validado
     * @return las alertas del paciente
     */
    public List<AlertaDto> getAlertas(Jwt jwt) {
        return aiAlertServiceClient.listAlertas(resolvePatientId(jwt));
    }

    /**
     * Lista las alertas de IA no leídas del paciente autenticado.
     *
     * @param jwt ID Token de Firebase ya validado
     * @return las alertas no leídas del paciente
     */
    public List<AlertaDto> getAlertasNoLeidas(Jwt jwt) {
        return aiAlertServiceClient.listAlertasNoLeidas(resolvePatientId(jwt));
    }

    /**
     * Marca una alerta como leída, verificando antes que pertenezca al paciente autenticado.
     *
     * @param jwt      ID Token de Firebase ya validado
     * @param idAlerta identificador de la alerta
     * @throws UpstreamErrorException si la alerta no existe o pertenece a otro paciente
     */
    public void marcarAlertaLeida(Jwt jwt, Long idAlerta) {
        assertAlertaOwnedByCurrentPatient(jwt, idAlerta);
        aiAlertServiceClient.marcarAlertaLeida(idAlerta);
    }

    /**
     * Marca todas las alertas del paciente autenticado como leídas.
     *
     * @param jwt ID Token de Firebase ya validado
     */
    public void marcarTodasAlertasLeidas(Jwt jwt) {
        aiAlertServiceClient.marcarTodasAlertasLeidas(resolvePatientId(jwt));
    }

    /**
     * Lista todas las recomendaciones alimentarias del paciente autenticado.
     *
     * @param jwt ID Token de Firebase ya validado
     * @return las recomendaciones del paciente
     */
    public List<RecomendacionDto> getRecomendaciones(Jwt jwt) {
        return aiAlertServiceClient.listRecomendaciones(resolvePatientId(jwt));
    }

    /**
     * Lista las recomendaciones alimentarias no leídas del paciente autenticado.
     *
     * @param jwt ID Token de Firebase ya validado
     * @return las recomendaciones no leídas del paciente
     */
    public List<RecomendacionDto> getRecomendacionesNoLeidas(Jwt jwt) {
        return aiAlertServiceClient.listRecomendacionesNoLeidas(resolvePatientId(jwt));
    }

    /**
     * Marca una recomendación como leída, verificando antes que pertenezca al paciente autenticado.
     *
     * @param jwt             ID Token de Firebase ya validado
     * @param idRecomendacion identificador de la recomendación
     * @throws UpstreamErrorException si la recomendación no existe o pertenece a otro paciente
     */
    public void marcarRecomendacionLeida(Jwt jwt, Long idRecomendacion) {
        assertRecomendacionOwnedByCurrentPatient(jwt, idRecomendacion);
        aiAlertServiceClient.marcarRecomendacionLeida(idRecomendacion);
    }

    /**
     * Marca todas las recomendaciones del paciente autenticado como leídas.
     *
     * @param jwt ID Token de Firebase ya validado
     */
    public void marcarTodasRecomendacionesLeidas(Jwt jwt) {
        aiAlertServiceClient.marcarTodasRecomendacionesLeidas(resolvePatientId(jwt));
    }

    /**
     * Verifica que una alerta pertenezca al paciente autenticado, recorriendo
     * su listado completo (la API de {@code ai-alert-service} no expone esa
     * validación por su cuenta).
     *
     * @param jwt      ID Token de Firebase ya validado
     * @param idAlerta identificador de la alerta a verificar
     * @throws UpstreamErrorException si la alerta no aparece en el listado del paciente
     */
    private void assertAlertaOwnedByCurrentPatient(Jwt jwt, Long idAlerta) {
        Long idPaciente = resolvePatientId(jwt);
        boolean owned = aiAlertServiceClient.listAlertas(idPaciente).stream()
                .anyMatch(alerta -> idAlerta.equals(alerta.getIdAlertaIa()));
        if (!owned) {
            throw new UpstreamErrorException(HttpStatus.NOT_FOUND, "Alerta no encontrada");
        }
    }

    /**
     * Verifica que una recomendación pertenezca al paciente autenticado,
     * recorriendo su listado completo.
     *
     * @param jwt             ID Token de Firebase ya validado
     * @param idRecomendacion identificador de la recomendación a verificar
     * @throws UpstreamErrorException si la recomendación no aparece en el listado del paciente
     */
    private void assertRecomendacionOwnedByCurrentPatient(Jwt jwt, Long idRecomendacion) {
        Long idPaciente = resolvePatientId(jwt);
        boolean owned = aiAlertServiceClient.listRecomendaciones(idPaciente).stream()
                .anyMatch(recomendacion -> idRecomendacion.equals(recomendacion.getIdRecomendacion()));
        if (!owned) {
            throw new UpstreamErrorException(HttpStatus.NOT_FOUND, "Recomendación no encontrada");
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
