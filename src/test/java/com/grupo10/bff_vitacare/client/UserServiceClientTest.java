package com.grupo10.bff_vitacare.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.grupo10.bff_vitacare.dto.AuthenticatedUserDto;
import com.grupo10.bff_vitacare.exception.RegistrationConflictException;
import com.grupo10.bff_vitacare.exception.UpstreamErrorException;
import com.grupo10.bff_vitacare.exception.UpstreamUserNotFoundException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

class UserServiceClientTest {

    private MockRestServiceServer server;
    private UserServiceClient client;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder();
        server = MockRestServiceServer.bindTo(builder).build();
        client = new UserServiceClient(builder, "http://user-service");
    }

    @Test
    void findByFirebaseUidReturnsTheUser() {
        server.expect(requestTo("http://user-service/api/users/firebase/uid-1"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(
                        "{\"id\":1,\"correo\":\"a@b.cl\",\"rol\":\"PACIENTE\",\"activo\":1}",
                        MediaType.APPLICATION_JSON));

        AuthenticatedUserDto user = client.findByFirebaseUid("uid-1");

        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getCorreo()).isEqualTo("a@b.cl");
    }

    @Test
    void findByFirebaseUidThrowsWhenNotSynced() {
        server.expect(requestTo("http://user-service/api/users/firebase/uid-missing"))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        assertThatThrownBy(() -> client.findByFirebaseUid("uid-missing"))
                .isInstanceOf(UpstreamUserNotFoundException.class);
    }

    @Test
    void tryFindByFirebaseUidReturnsEmptyWhenNotSynced() {
        server.expect(requestTo("http://user-service/api/users/firebase/uid-missing"))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        Optional<AuthenticatedUserDto> result = client.tryFindByFirebaseUid("uid-missing");

        assertThat(result).isEmpty();
    }

    @Test
    void tryFindByFirebaseUidReturnsUserWhenSynced() {
        server.expect(requestTo("http://user-service/api/users/firebase/uid-1"))
                .andRespond(withSuccess("{\"id\":1,\"correo\":\"a@b.cl\"}", MediaType.APPLICATION_JSON));

        Optional<AuthenticatedUserDto> result = client.tryFindByFirebaseUid("uid-1");

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
    }

    @Test
    void createUserPostsToRegisterEndpoint() {
        server.expect(requestTo("http://user-service/api/users/register"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(jsonPath("$.correo").value("a@b.cl"))
                .andExpect(jsonPath("$.firebaseUid").value("uid-1"))
                .andExpect(jsonPath("$.rol").value("PACIENTE"))
                .andRespond(withSuccess("{\"id\":1,\"correo\":\"a@b.cl\",\"rol\":\"PACIENTE\"}", MediaType.APPLICATION_JSON));

        AuthenticatedUserDto user = client.createUser("a@b.cl", "uid-1", "PACIENTE");

        assertThat(user.getId()).isEqualTo(1L);
    }

    @Test
    void createUserThrowsConflictWhenAlreadyExists() {
        server.expect(requestTo("http://user-service/api/users/register"))
                .andRespond(withStatus(HttpStatus.CONFLICT));

        assertThatThrownBy(() -> client.createUser("a@b.cl", "uid-1", "PACIENTE"))
                .isInstanceOf(RegistrationConflictException.class);
    }

    @Test
    void deleteUserByFirebaseUidCallsDeleteEndpoint() {
        server.expect(requestTo("http://user-service/api/users/firebase/uid-1"))
                .andExpect(method(HttpMethod.DELETE))
                .andRespond(withStatus(HttpStatus.NO_CONTENT));

        client.deleteUserByFirebaseUid("uid-1");

        server.verify();
    }

    @Test
    void deleteUserByFirebaseUidThrowsOnError() {
        server.expect(requestTo("http://user-service/api/users/firebase/uid-1"))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST));

        assertThatThrownBy(() -> client.deleteUserByFirebaseUid("uid-1"))
                .isInstanceOf(UpstreamErrorException.class);
    }
}
