package com.grupo10.bff_vitacare.client;

import java.util.Map;
import java.util.Optional;

import com.grupo10.bff_vitacare.dto.AuthenticatedUserDto;
import com.grupo10.bff_vitacare.exception.RegistrationConflictException;
import com.grupo10.bff_vitacare.exception.UpstreamErrorException;
import com.grupo10.bff_vitacare.exception.UpstreamUserNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * Cliente HTTP hacia {@code user-service}, usado para resolver el usuario
 * interno a partir del UID de Firebase presente en el ID Token validado, y
 * para sincronizar usuarios nuevos tras el signup en Firebase.
 */
@Component
public class UserServiceClient {

    private final RestClient restClient;

    public UserServiceClient(RestClient.Builder restClientBuilder,
                              @Value("${user-service.base-url}") String userServiceBaseUrl) {
        this.restClient = restClientBuilder.baseUrl(userServiceBaseUrl).build();
    }

    /**
     * Busca el usuario sincronizado en {@code tb_usuario} por su UID de Firebase.
     *
     * @param firebaseUid UID de Firebase a buscar
     * @return los datos del usuario encontrado
     * @throws UpstreamUserNotFoundException si el UID no está sincronizado en {@code user-service}
     */
    public AuthenticatedUserDto findByFirebaseUid(String firebaseUid) {
        return restClient.get()
                .uri("/api/users/firebase/{firebaseUid}", firebaseUid)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new UpstreamUserNotFoundException("Usuario no sincronizado: " + firebaseUid);
                })
                .body(AuthenticatedUserDto.class);
    }

    /**
     * Igual que {@link #findByFirebaseUid(String)}, pero devuelve un
     * {@link Optional} vacío en vez de lanzar excepción cuando el UID no
     * está sincronizado todavía. Pensado para flujos de registro idempotentes.
     *
     * @param firebaseUid UID de Firebase a buscar
     * @return el usuario si ya está sincronizado, o vacío si aún no existe
     */
    public Optional<AuthenticatedUserDto> tryFindByFirebaseUid(String firebaseUid) {
        try {
            return Optional.of(findByFirebaseUid(firebaseUid));
        } catch (UpstreamUserNotFoundException ex) {
            return Optional.empty();
        }
    }

    /**
     * Sincroniza un usuario nuevo en {@code tb_usuario} tras el signup en Firebase.
     *
     * @param correo      correo del usuario
     * @param firebaseUid UID de Firebase del usuario
     * @param rol         rol asignado al usuario
     * @return los datos del usuario recién creado
     * @throws RegistrationConflictException si ya existe un usuario con ese correo o UID
     */
    public AuthenticatedUserDto createUser(String correo, String firebaseUid, String rol) {
        return restClient.post()
                .uri("/api/users/register")
                .body(Map.of("correo", correo, "firebaseUid", firebaseUid, "rol", rol))
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new RegistrationConflictException("No fue posible registrar el usuario: " + correo);
                })
                .body(AuthenticatedUserDto.class);
    }

    /**
     * Elimina el usuario sincronizado en {@code tb_usuario} por su UID de Firebase.
     *
     * @param firebaseUid UID de Firebase del usuario a eliminar
     */
    public void deleteUserByFirebaseUid(String firebaseUid) {
        restClient.delete()
                .uri("/api/users/firebase/{firebaseUid}", firebaseUid)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new UpstreamErrorException(response.getStatusCode(), "No fue posible eliminar el usuario");
                })
                .toBodilessEntity();
    }

}
