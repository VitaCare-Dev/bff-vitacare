package com.grupo10.bff_vitacare.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.grupo10.bff_vitacare.client.UserServiceClient;
import com.grupo10.bff_vitacare.dto.AuthenticatedUserDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;

@ExtendWith(MockitoExtension.class)
class AuthContextServiceTest {

    @Mock
    private UserServiceClient userServiceClient;

    @InjectMocks
    private AuthContextService authContextService;

    @Test
    void resolveCurrentUserSetsTheFirebaseUidFromTheToken() {
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .claim("sub", "uid-1")
                .build();

        AuthenticatedUserDto user = new AuthenticatedUserDto();
        user.setId(1L);
        when(userServiceClient.findByFirebaseUid("uid-1")).thenReturn(user);

        AuthenticatedUserDto result = authContextService.resolveCurrentUser(jwt);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getFirebaseUid()).isEqualTo("uid-1");
    }
}
