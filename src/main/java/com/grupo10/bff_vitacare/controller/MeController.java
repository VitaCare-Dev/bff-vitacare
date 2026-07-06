package com.grupo10.bff_vitacare.controller;

import com.grupo10.bff_vitacare.dto.AuthenticatedUserDto;
import com.grupo10.bff_vitacare.service.AuthContextService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Expone la identidad del usuario autenticado, resuelta a partir del ID
 * Token de Firebase. Sirve como endpoint de verificación del flujo completo
 * de autenticación del BFF.
 */
@RestController
@RequestMapping("/api")
public class MeController {

    private final AuthContextService authContextService;

    /**
     * @param authContextService servicio que resuelve el usuario a partir del token
     */
    public MeController(AuthContextService authContextService) {
        this.authContextService = authContextService;
    }

    /**
     * {@code GET /api/me}: devuelve los datos del usuario autenticado.
     *
     * @param jwt ID Token de Firebase, inyectado por Spring Security tras validarlo
     * @return 200 con los datos del usuario autenticado
     */
    @GetMapping("/me")
    public ResponseEntity<AuthenticatedUserDto> getCurrentUser(@AuthenticationPrincipal Jwt jwt) {
        AuthenticatedUserDto user = authContextService.resolveCurrentUser(jwt);
        return ResponseEntity.ok(user);
    }

}
