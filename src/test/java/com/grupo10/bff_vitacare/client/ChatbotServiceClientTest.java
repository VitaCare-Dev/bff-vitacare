package com.grupo10.bff_vitacare.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

class ChatbotServiceClientTest {

    private MockRestServiceServer server;
    private ChatbotServiceClient client;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder();
        server = MockRestServiceServer.bindTo(builder).build();
        client = new ChatbotServiceClient(builder, "http://chatbot-service");
    }

    @Test
    void sendMessagePostsToChatEndpointAndReturnsTheReply() {
        server.expect(requestTo("http://chatbot-service/api/chat/enviar"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(jsonPath("$.idUsuario").value(1))
                .andExpect(jsonPath("$.mensaje").value("hola"))
                .andRespond(withSuccess("{\"respuesta\":\"hola, ¿en qué puedo ayudarte?\"}", MediaType.APPLICATION_JSON));

        String reply = client.sendMessage(1L, "hola");

        assertThat(reply).isEqualTo("hola, ¿en qué puedo ayudarte?");
    }
}
