package com.grupo10.bff_vitacare.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.grupo10.bff_vitacare.dto.AuthenticatedUserDto;
import com.grupo10.bff_vitacare.service.AuthContextService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;

@ExtendWith(MockitoExtension.class)
class MeControllerTest {

    @Mock
    private AuthContextService authContextService;

    @InjectMocks
    private MeController meController;

    @Test
    void getCurrentUserReturnsTheAuthenticatedUser() {
        Jwt jwt = Jwt.withTokenValue("token").header("alg", "none").claim("sub", "uid-1").build();
        AuthenticatedUserDto user = new AuthenticatedUserDto();
        user.setId(1L);
        when(authContextService.resolveCurrentUser(jwt)).thenReturn(user);

        ResponseEntity<AuthenticatedUserDto> response = meController.getCurrentUser(jwt);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getId()).isEqualTo(1L);
    }
}
