package com.grupo10.bff_vitacare.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.grupo10.bff_vitacare.client.ChatbotServiceClient;
import com.grupo10.bff_vitacare.dto.AuthenticatedUserDto;
import com.grupo10.bff_vitacare.dto.ChatMessageRequestDto;
import com.grupo10.bff_vitacare.dto.ChatMessageResponseDto;
import com.grupo10.bff_vitacare.service.AuthContextService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;

@ExtendWith(MockitoExtension.class)
class ChatControllerTest {

    @Mock
    private AuthContextService authContextService;

    @Mock
    private ChatbotServiceClient chatbotServiceClient;

    @InjectMocks
    private ChatController chatController;

    @Test
    void sendMessageResolvesTheUserAndDelegatesToTheChatbot() {
        Jwt jwt = Jwt.withTokenValue("token").header("alg", "none").claim("sub", "uid-1").build();
        AuthenticatedUserDto user = new AuthenticatedUserDto();
        user.setId(1L);
        when(authContextService.resolveCurrentUser(jwt)).thenReturn(user);

        ChatMessageRequestDto request = new ChatMessageRequestDto();
        request.setMensaje("hola");
        when(chatbotServiceClient.sendMessage(1L, "hola")).thenReturn("hola, ¿en qué puedo ayudarte?");

        ResponseEntity<ChatMessageResponseDto> response = chatController.sendMessage(jwt, request);

        assertThat(response.getBody().getRespuesta()).isEqualTo("hola, ¿en qué puedo ayudarte?");
    }
}
