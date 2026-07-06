package com.grupo10.bff_vitacare.controller;

import com.grupo10.bff_vitacare.client.ChatbotServiceClient;
import com.grupo10.bff_vitacare.dto.AuthenticatedUserDto;
import com.grupo10.bff_vitacare.dto.ChatMessageRequestDto;
import com.grupo10.bff_vitacare.dto.ChatMessageResponseDto;
import com.grupo10.bff_vitacare.service.AuthContextService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Orquesta el envío de mensajes al chatbot IA para el usuario autenticado.
 */
@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final AuthContextService authContextService;
    private final ChatbotServiceClient chatbotServiceClient;

    /**
     * @param authContextService   servicio que resuelve el usuario a partir del token
     * @param chatbotServiceClient cliente hacia {@code chatbot-service}
     */
    public ChatController(AuthContextService authContextService, ChatbotServiceClient chatbotServiceClient) {
        this.authContextService = authContextService;
        this.chatbotServiceClient = chatbotServiceClient;
    }

    /**
     * {@code POST /api/chat}: envía un mensaje del usuario autenticado al chatbot IA.
     *
     * @param jwt     ID Token de Firebase, inyectado por Spring Security tras validarlo
     * @param request mensaje a enviar
     * @return 200 con la respuesta generada por el chatbot
     */
    @PostMapping
    public ResponseEntity<ChatMessageResponseDto> sendMessage(@AuthenticationPrincipal Jwt jwt,
                                                               @RequestBody ChatMessageRequestDto request) {
        AuthenticatedUserDto user = authContextService.resolveCurrentUser(jwt);
        String respuesta = chatbotServiceClient.sendMessage(user.getId(), request.getMensaje());

        ChatMessageResponseDto response = new ChatMessageResponseDto();
        response.setRespuesta(respuesta);
        return ResponseEntity.ok(response);
    }

}
