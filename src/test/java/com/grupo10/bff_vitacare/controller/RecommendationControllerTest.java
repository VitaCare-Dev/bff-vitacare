package com.grupo10.bff_vitacare.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.grupo10.bff_vitacare.dto.RecomendacionDto;
import com.grupo10.bff_vitacare.service.AiAlertOrchestrationService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;

@ExtendWith(MockitoExtension.class)
class RecommendationControllerTest {

    @Mock
    private AiAlertOrchestrationService aiAlertOrchestrationService;

    @InjectMocks
    private RecommendationController recommendationController;

    private final Jwt jwt = Jwt.withTokenValue("token").header("alg", "none").claim("sub", "uid-1").build();

    @Test
    void getRecommendationsReturnsTheList() {
        when(aiAlertOrchestrationService.getRecomendaciones(jwt)).thenReturn(List.of(new RecomendacionDto()));

        assertThat(recommendationController.getRecommendations(jwt).getBody()).hasSize(1);
    }

    @Test
    void getUnreadRecommendationsReturnsTheList() {
        when(aiAlertOrchestrationService.getRecomendacionesNoLeidas(jwt)).thenReturn(List.of(new RecomendacionDto()));

        assertThat(recommendationController.getUnreadRecommendations(jwt).getBody()).hasSize(1);
    }

    @Test
    void markRecommendationAsReadReturns204() {
        ResponseEntity<Void> response = recommendationController.markRecommendationAsRead(jwt, 1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(aiAlertOrchestrationService).marcarRecomendacionLeida(jwt, 1L);
    }

    @Test
    void markAllRecommendationsAsReadReturns204() {
        ResponseEntity<Void> response = recommendationController.markAllRecommendationsAsRead(jwt);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(aiAlertOrchestrationService).marcarTodasRecomendacionesLeidas(jwt);
    }
}
