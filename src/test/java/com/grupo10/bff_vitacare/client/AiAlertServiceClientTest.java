package com.grupo10.bff_vitacare.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.grupo10.bff_vitacare.dto.AlertaDto;
import com.grupo10.bff_vitacare.dto.RecomendacionDto;
import com.grupo10.bff_vitacare.exception.UpstreamErrorException;
import java.lang.reflect.Method;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

class AiAlertServiceClientTest {

    private MockRestServiceServer server;
    private AiAlertServiceClient client;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder();
        server = MockRestServiceServer.bindTo(builder).build();
        client = new AiAlertServiceClient(builder, "http://ai-alert-service", "test-key");
    }

    @Test
    void listAlertasAppendsTheFunctionKey() {
        server.expect(requestTo("http://ai-alert-service/api/alertas/1?code=test-key"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("[{\"idAlertaIa\":1}]", MediaType.APPLICATION_JSON));

        List<AlertaDto> alerts = client.listAlertas(1L);

        assertThat(alerts).hasSize(1);
    }

    @Test
    void listAlertasNoLeidasAppendsTheFunctionKey() {
        server.expect(requestTo("http://ai-alert-service/api/alertas/1/no-leidas?code=test-key"))
                .andRespond(withSuccess("[]", MediaType.APPLICATION_JSON));

        List<AlertaDto> alerts = client.listAlertasNoLeidas(1L);

        assertThat(alerts).isEmpty();
    }

    @Test
    void marcarAlertaLeidaPutsToTheReadEndpoint() {
        server.expect(requestTo("http://ai-alert-service/api/alertas/1/leer?code=test-key"))
                .andExpect(method(HttpMethod.PUT))
                .andRespond(withStatus(HttpStatus.NO_CONTENT));

        client.marcarAlertaLeida(1L);

        server.verify();
    }

    @Test
    void marcarAlertaLeidaThrowsWhenNotFound() {
        server.expect(requestTo("http://ai-alert-service/api/alertas/1/leer?code=test-key"))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        assertThatThrownBy(() -> client.marcarAlertaLeida(1L))
                .isInstanceOf(UpstreamErrorException.class);
    }

    @Test
    void marcarTodasAlertasLeidasPutsToTheReadAllEndpoint() {
        server.expect(requestTo("http://ai-alert-service/api/alertas/paciente/1/leer-todas?code=test-key"))
                .andExpect(method(HttpMethod.PUT))
                .andRespond(withStatus(HttpStatus.NO_CONTENT));

        client.marcarTodasAlertasLeidas(1L);

        server.verify();
    }

    @Test
    void listRecomendacionesAppendsTheFunctionKey() {
        server.expect(requestTo("http://ai-alert-service/api/recomendaciones/1?code=test-key"))
                .andRespond(withSuccess("[{\"idRecomendacion\":1}]", MediaType.APPLICATION_JSON));

        List<RecomendacionDto> result = client.listRecomendaciones(1L);

        assertThat(result).hasSize(1);
    }

    @Test
    void listRecomendacionesNoLeidasAppendsTheFunctionKey() {
        server.expect(requestTo("http://ai-alert-service/api/recomendaciones/1/no-leidas?code=test-key"))
                .andRespond(withSuccess("[]", MediaType.APPLICATION_JSON));

        List<RecomendacionDto> result = client.listRecomendacionesNoLeidas(1L);

        assertThat(result).isEmpty();
    }

    @Test
    void marcarRecomendacionLeidaPutsToTheReadEndpoint() {
        server.expect(requestTo("http://ai-alert-service/api/recomendaciones/1/leer?code=test-key"))
                .andExpect(method(HttpMethod.PUT))
                .andRespond(withStatus(HttpStatus.NO_CONTENT));

        client.marcarRecomendacionLeida(1L);

        server.verify();
    }

    @Test
    void marcarRecomendacionLeidaThrowsWhenNotFound() {
        server.expect(requestTo("http://ai-alert-service/api/recomendaciones/1/leer?code=test-key"))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        assertThatThrownBy(() -> client.marcarRecomendacionLeida(1L))
                .isInstanceOf(UpstreamErrorException.class);
    }

    @Test
    void marcarTodasRecomendacionesLeidasPutsToTheReadAllEndpoint() {
        server.expect(requestTo("http://ai-alert-service/api/recomendaciones/paciente/1/leer-todas?code=test-key"))
                .andExpect(method(HttpMethod.PUT))
                .andRespond(withStatus(HttpStatus.NO_CONTENT));

        client.marcarTodasRecomendacionesLeidas(1L);

        server.verify();
    }

    @Test
    void withKeyAppendsWithAmpersandWhenThePathAlreadyHasAQueryString() throws Exception {
        // Ninguna llamada pública pasa hoy una ruta con "?" previo, pero el
        // método está escrito para soportarlo: se prueba directo por reflexión.
        Method withKey = AiAlertServiceClient.class.getDeclaredMethod("withKey", String.class);
        withKey.setAccessible(true);

        String result = (String) withKey.invoke(client, "/api/alertas/1?foo=bar");

        assertThat(result).isEqualTo("/api/alertas/1?foo=bar&code=test-key");
    }
}
