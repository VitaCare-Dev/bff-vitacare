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

    /**
     * @param aiAlertOrchestrationService servicio que orquesta alertas y recomendaciones de IA
     */
    public AlertController(AiAlertOrchestrationService aiAlertOrchestrationService) {
        this.aiAlertOrchestrationService = aiAlertOrchestrationService;
    }

    /**
     * {@code GET /api/alerts}: lista todas las alertas de IA del paciente autenticado.
     *
     * @param jwt ID Token de Firebase, inyectado por Spring Security tras validarlo
     * @return 200 con las alertas del paciente
     */
    @GetMapping
    public ResponseEntity<List<AlertaDto>> getAlerts(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(aiAlertOrchestrationService.getAlertas(jwt));
    }

    /**
     * {@code GET /api/alerts/unread}: lista las alertas de IA no leídas del paciente autenticado.
     *
     * @param jwt ID Token de Firebase, inyectado por Spring Security tras validarlo
     * @return 200 con las alertas no leídas del paciente
     */
    @GetMapping("/unread")
    public ResponseEntity<List<AlertaDto>> getUnreadAlerts(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(aiAlertOrchestrationService.getAlertasNoLeidas(jwt));
    }

    /**
     * {@code PUT /api/alerts/{id}/read}: marca una alerta como leída.
     *
     * @param jwt ID Token de Firebase, inyectado por Spring Security tras validarlo
     * @param id  identificador de la alerta
     * @return 204 sin contenido
     */
    @PutMapping("/{id}/read")
    public ResponseEntity<Void> markAlertAsRead(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id) {
        aiAlertOrchestrationService.marcarAlertaLeida(jwt, id);
        return ResponseEntity.noContent().build();
    }

    /**
     * {@code PUT /api/alerts/read-all}: marca todas las alertas del paciente autenticado como leídas.
     *
     * @param jwt ID Token de Firebase, inyectado por Spring Security tras validarlo
     * @return 204 sin contenido
     */
    @PutMapping("/read-all")
    public ResponseEntity<Void> markAllAlertsAsRead(@AuthenticationPrincipal Jwt jwt) {
        aiAlertOrchestrationService.marcarTodasAlertasLeidas(jwt);
        return ResponseEntity.noContent().build();
    }

}
