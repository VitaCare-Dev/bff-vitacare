package com.grupo10.bff_vitacare.service;

import com.grupo10.bff_vitacare.client.UserServiceClient;
import com.grupo10.bff_vitacare.dto.AuthenticatedUserDto;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

/**
 * Resuelve la identidad de dominio (usuario interno) a partir del ID Token
 * de Firebase ya validado por {@code SecurityConfig}.
 */
@Service
public class AuthContextService {

    private final UserServiceClient userServiceClient;

    /**
     * @param userServiceClient cliente hacia {@code user-service}
     */
    public AuthContextService(UserServiceClient userServiceClient) {
        this.userServiceClient = userServiceClient;
    }

    /**
     * Resuelve el usuario interno asociado al {@link Jwt} autenticado.
     *
     * @param jwt token de Firebase ya validado por Spring Security
     * @return los datos del usuario, incluyendo el {@code firebase_uid} (claim {@code sub})
     */
    public AuthenticatedUserDto resolveCurrentUser(Jwt jwt) {
        String firebaseUid = jwt.getSubject();
        AuthenticatedUserDto user = userServiceClient.findByFirebaseUid(firebaseUid);
        user.setFirebaseUid(firebaseUid);
        return user;
    }

}
