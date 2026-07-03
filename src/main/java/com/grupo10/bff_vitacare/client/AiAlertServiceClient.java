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

    public AiAlertServiceClient(RestClient.Builder restClientBuilder,
                                 @Value("${ai-alert-service.base-url}") String baseUrl,
                                 @Value("${ai-alert-service.function-key}") String functionKey) {
        this.restClient = restClientBuilder.baseUrl(baseUrl).build();
        this.functionKey = functionKey;
    }

    private String withKey(String path) {
        return path + (path.contains("?") ? "&" : "?") + "code=" + functionKey;
    }

    public List<AlertaDto> listAlertas(Long idPaciente) {
        return restClient.get()
                .uri(withKey("/api/alertas/{idPaciente}"), idPaciente)
                .retrieve()
                .body(new ParameterizedTypeReference<List<AlertaDto>>() {
                });
    }

    public List<AlertaDto> listAlertasNoLeidas(Long idPaciente) {
        return restClient.get()
                .uri(withKey("/api/alertas/{idPaciente}/no-leidas"), idPaciente)
                .retrieve()
                .body(new ParameterizedTypeReference<List<AlertaDto>>() {
                });
    }

    public void marcarAlertaLeida(Long idAlerta) {
        restClient.put()
                .uri(withKey("/api/alertas/{idAlerta}/leer"), idAlerta)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new UpstreamErrorException(response.getStatusCode(), "Alerta no encontrada");
                })
                .toBodilessEntity();
    }

    public void marcarTodasAlertasLeidas(Long idPaciente) {
        restClient.put()
                .uri(withKey("/api/alertas/paciente/{idPaciente}/leer-todas"), idPaciente)
                .retrieve()
                .toBodilessEntity();
    }

    public List<RecomendacionDto> listRecomendaciones(Long idPaciente) {
        return restClient.get()
                .uri(withKey("/api/recomendaciones/{idPaciente}"), idPaciente)
                .retrieve()
                .body(new ParameterizedTypeReference<List<RecomendacionDto>>() {
                });
    }

    public List<RecomendacionDto> listRecomendacionesNoLeidas(Long idPaciente) {
        return restClient.get()
                .uri(withKey("/api/recomendaciones/{idPaciente}/no-leidas"), idPaciente)
                .retrieve()
                .body(new ParameterizedTypeReference<List<RecomendacionDto>>() {
                });
    }

    public void marcarRecomendacionLeida(Long idRecomendacion) {
        restClient.put()
                .uri(withKey("/api/recomendaciones/{idRecomendacion}/leer"), idRecomendacion)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new UpstreamErrorException(response.getStatusCode(), "Recomendación no encontrada");
                })
                .toBodilessEntity();
    }

    public void marcarTodasRecomendacionesLeidas(Long idPaciente) {
        restClient.put()
                .uri(withKey("/api/recomendaciones/paciente/{idPaciente}/leer-todas"), idPaciente)
                .retrieve()
                .toBodilessEntity();
    }

}
