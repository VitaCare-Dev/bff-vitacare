package com.grupo10.bff_vitacare.controller;

import java.util.List;

import com.grupo10.bff_vitacare.dto.AlertaDto;
import com.grupo10.bff_vitacare.service.AiAlertOrchestrationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Expone las alertas de IA proactiva del paciente autenticado, generadas por
 * {@code ai-alert-service} a partir del análisis automático de sus mediciones.
 */
@RestController
@RequestMapping("/api/alerts")
public class AlertController {

    private final AiAlertOrchestrationService aiAlertOrchestrationService;

    public AlertController(AiAlertOrchestrationService aiAlertOrchestrationService) {
        this.aiAlertOrchestrationService = aiAlertOrchestrationService;
    }

    @GetMapping
    public ResponseEntity<List<AlertaDto>> getAlerts(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(aiAlertOrchestrationService.getAlertas(jwt));
    }

    @GetMapping("/unread")
    public ResponseEntity<List<AlertaDto>> getUnreadAlerts(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(aiAlertOrchestrationService.getAlertasNoLeidas(jwt));
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<Void> markAlertAsRead(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id) {
        aiAlertOrchestrationService.marcarAlertaLeida(jwt, id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/read-all")
    public ResponseEntity<Void> markAllAlertsAsRead(@AuthenticationPrincipal Jwt jwt) {
        aiAlertOrchestrationService.marcarTodasAlertasLeidas(jwt);
        return ResponseEntity.noContent().build();
    }

}
