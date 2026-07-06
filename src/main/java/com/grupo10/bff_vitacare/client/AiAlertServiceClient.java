package com.grupo10.bff_vitacare.client;

import java.util.List;

import com.grupo10.bff_vitacare.dto.AlertaDto;
import com.grupo10.bff_vitacare.dto.RecomendacionDto;
import com.grupo10.bff_vitacare.exception.UpstreamErrorException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * Cliente HTTP hacia {@code ai-alert-service} (Azure Functions de IA
 * proactiva). Las funciones HTTP allá son {@code authLevel = FUNCTION}: cada
 * llamada necesita la function key como parámetro {@code code}, que vive
 * solo en el servidor del BFF, nunca en la app móvil.
 */
@Component
public class AiAlertServiceClient {

    private final RestClient restClient;
    private final String functionKey;

    /**
     * @param restClientBuilder builder de {@link RestClient} inyectado por Spring
     * @param baseUrl           URL base de {@code ai-alert-service}
     * @param functionKey       clave de función de Azure requerida para autenticar cada llamada
     */
    public AiAlertServiceClient(RestClient.Builder restClientBuilder,
                                 @Value("${ai-alert-service.base-url}") String baseUrl,
                                 @Value("${ai-alert-service.function-key}") String functionKey) {
        this.restClient = restClientBuilder.baseUrl(baseUrl).build();
        this.functionKey = functionKey;
    }

    /**
     * Agrega la function key de Azure como parámetro de consulta {@code code}.
     *
     * @param path ruta relativa del endpoint, con o sin query string previa
     * @return la ruta con el parámetro {@code code} anexado
     */
    private String withKey(String path) {
        return path + (path.contains("?") ? "&" : "?") + "code=" + functionKey;
    }

    /**
     * Lista todas las alertas de IA generadas para un paciente.
     *
     * @param idPaciente identificador del paciente
     * @return las alertas del paciente (vacía si no tiene ninguna)
     */
    public List<AlertaDto> listAlertas(Long idPaciente) {
        return restClient.get()
                .uri(withKey("/api/alertas/{idPaciente}"), idPaciente)
                .retrieve()
                .body(new ParameterizedTypeReference<List<AlertaDto>>() {
                });
    }

    /**
     * Lista las alertas de IA no leídas de un paciente.
     *
     * @param idPaciente identificador del paciente
     * @return las alertas no leídas del paciente (vacía si no tiene ninguna)
     */
    public List<AlertaDto> listAlertasNoLeidas(Long idPaciente) {
        return restClient.get()
                .uri(withKey("/api/alertas/{idPaciente}/no-leidas"), idPaciente)
                .retrieve()
                .body(new ParameterizedTypeReference<List<AlertaDto>>() {
                });
    }

    /**
     * Marca una alerta específica como leída.
     *
     * @param idAlerta identificador de la alerta
     * @throws UpstreamErrorException si la alerta no existe
     */
    public void marcarAlertaLeida(Long idAlerta) {
        restClient.put()
                .uri(withKey("/api/alertas/{idAlerta}/leer"), idAlerta)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new UpstreamErrorException(response.getStatusCode(), "Alerta no encontrada");
                })
                .toBodilessEntity();
    }

    /**
     * Marca todas las alertas de un paciente como leídas.
     *
     * @param idPaciente identificador del paciente
     */
    public void marcarTodasAlertasLeidas(Long idPaciente) {
        restClient.put()
                .uri(withKey("/api/alertas/paciente/{idPaciente}/leer-todas"), idPaciente)
                .retrieve()
                .toBodilessEntity();
    }

    /**
     * Lista todas las recomendaciones alimentarias generadas para un paciente.
     *
     * @param idPaciente identificador del paciente
     * @return las recomendaciones del paciente (vacía si no tiene ninguna)
     */
    public List<RecomendacionDto> listRecomendaciones(Long idPaciente) {
        return restClient.get()
                .uri(withKey("/api/recomendaciones/{idPaciente}"), idPaciente)
                .retrieve()
                .body(new ParameterizedTypeReference<List<RecomendacionDto>>() {
                });
    }

    /**
     * Lista las recomendaciones alimentarias no leídas de un paciente.
     *
     * @param idPaciente identificador del paciente
     * @return las recomendaciones no leídas del paciente (vacía si no tiene ninguna)
     */
    public List<RecomendacionDto> listRecomendacionesNoLeidas(Long idPaciente) {
        return restClient.get()
                .uri(withKey("/api/recomendaciones/{idPaciente}/no-leidas"), idPaciente)
                .retrieve()
                .body(new ParameterizedTypeReference<List<RecomendacionDto>>() {
                });
    }

    /**
     * Marca una recomendación específica como leída.
     *
     * @param idRecomendacion identificador de la recomendación
     * @throws UpstreamErrorException si la recomendación no existe
     */
    public void marcarRecomendacionLeida(Long idRecomendacion) {
        restClient.put()
                .uri(withKey("/api/recomendaciones/{idRecomendacion}/leer"), idRecomendacion)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new UpstreamErrorException(response.getStatusCode(), "Recomendación no encontrada");
                })
                .toBodilessEntity();
    }

    /**
     * Marca todas las recomendaciones de un paciente como leídas.
     *
     * @param idPaciente identificador del paciente
     */
    public void marcarTodasRecomendacionesLeidas(Long idPaciente) {
        restClient.put()
                .uri(withKey("/api/recomendaciones/paciente/{idPaciente}/leer-todas"), idPaciente)
                .retrieve()
                .toBodilessEntity();
    }

}
