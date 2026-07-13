package com.grupo10.bff_vitacare.client;

import java.util.HashMap;
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

    /**
     * @param restClientBuilder       builder de {@link RestClient} inyectado por Spring
     * @param chatbotServiceBaseUrl   URL base de {@code chatbot-service}
     */
    public ChatbotServiceClient(RestClient.Builder restClientBuilder,
                                 @Value("${chatbot-service.base-url}") String chatbotServiceBaseUrl) {
        this.restClient = restClientBuilder.baseUrl(chatbotServiceBaseUrl).build();
    }

    /**
     * Envía un mensaje del usuario al chatbot IA, incluyendo el contexto
     * médico del paciente (enfermedades/umbrales) para que la IA responda de
     * forma más pertinente a su condición.
     *
     * @param idUsuario        identificador interno del usuario autenticado
     * @param mensaje          mensaje del usuario
     * @param contextoPaciente descripción del contexto médico del paciente,
     *                         o {@code null} si no tiene enfermedades registradas
     * @return la respuesta generada por el chatbot
     */
    public String sendMessage(Long idUsuario, String mensaje, String contextoPaciente) {
        Map<String, Object> body = new HashMap<>();
        body.put("idUsuario", idUsuario);
        body.put("mensaje", mensaje);
        body.put("contextoPaciente", contextoPaciente);

        ChatMessageResponseDto response = restClient.post()
                .uri("/api/chat/enviar")
                .body(body)
                .retrieve()
                .body(ChatMessageResponseDto.class);
        return response.getRespuesta();
    }

}
