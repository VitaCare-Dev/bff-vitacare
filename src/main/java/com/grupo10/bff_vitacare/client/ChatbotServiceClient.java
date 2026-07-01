package com.grupo10.bff_vitacare.client;

import java.util.Map;

import com.grupo10.bff_vitacare.dto.ChatMessageResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * Cliente HTTP hacia {@code chatbot-service}. El servicio siempre responde
 * 200 (incluso ante errores propios, como texto en la respuesta), así que
 * no hay manejo especial de estados de error aquí.
 */
@Component
public class ChatbotServiceClient {

    private final RestClient restClient;

    public ChatbotServiceClient(RestClient.Builder restClientBuilder,
                                 @Value("${chatbot-service.base-url}") String chatbotServiceBaseUrl) {
        this.restClient = restClientBuilder.baseUrl(chatbotServiceBaseUrl).build();
    }

    /**
     * Envía un mensaje del usuario al chatbot IA.
     *
     * @param idUsuario identificador interno del usuario autenticado
     * @param mensaje   mensaje del usuario
     * @return la respuesta generada por el chatbot
     */
    public String sendMessage(Long idUsuario, String mensaje) {
        ChatMessageResponseDto response = restClient.post()
                .uri("/api/chat/enviar")
                .body(Map.of("idUsuario", idUsuario, "mensaje", mensaje))
                .retrieve()
                .body(ChatMessageResponseDto.class);
        return response.getRespuesta();
    }

}
