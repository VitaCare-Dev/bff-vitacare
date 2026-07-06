package com.grupo10.bff_vitacare.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.grupo10.bff_vitacare.dto.AlertaDto;
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
class AlertControllerTest {

    @Mock
    private AiAlertOrchestrationService aiAlertOrchestrationService;

    @InjectMocks
    private AlertController alertController;

    private final Jwt jwt = Jwt.withTokenValue("token").header("alg", "none").claim("sub", "uid-1").build();

    @Test
    void getAlertsReturnsTheList() {
        when(aiAlertOrchestrationService.getAlertas(jwt)).thenReturn(List.of(new AlertaDto()));

        assertThat(alertController.getAlerts(jwt).getBody()).hasSize(1);
    }

    @Test
    void getUnreadAlertsReturnsTheList() {
        when(aiAlertOrchestrationService.getAlertasNoLeidas(jwt)).thenReturn(List.of(new AlertaDto()));

        assertThat(alertController.getUnreadAlerts(jwt).getBody()).hasSize(1);
    }

    @Test
    void markAlertAsReadReturns204() {
        ResponseEntity<Void> response = alertController.markAlertAsRead(jwt, 1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(aiAlertOrchestrationService).marcarAlertaLeida(jwt, 1L);
    }

    @Test
    void markAllAlertsAsReadReturns204() {
        ResponseEntity<Void> response = alertController.markAllAlertsAsRead(jwt);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(aiAlertOrchestrationService).marcarTodasAlertasLeidas(jwt);
    }
}
