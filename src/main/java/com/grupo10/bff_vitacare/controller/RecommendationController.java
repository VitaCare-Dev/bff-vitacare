package com.grupo10.bff_vitacare.controller;

import java.util.List;

import com.grupo10.bff_vitacare.dto.RecomendacionDto;
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
 * Expone las recomendaciones alimentarias de IA del paciente autenticado,
 * generadas por {@code ai-alert-service} a partir de sus enfermedades crónicas.
 */
@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {

    private final AiAlertOrchestrationService aiAlertOrchestrationService;

    /**
     * @param aiAlertOrchestrationService servicio que orquesta alertas y recomendaciones de IA
     */
    public RecommendationController(AiAlertOrchestrationService aiAlertOrchestrationService) {
        this.aiAlertOrchestrationService = aiAlertOrchestrationService;
    }

    /**
     * {@code GET /api/recommendations}: lista todas las recomendaciones alimentarias del paciente autenticado.
     *
     * @param jwt ID Token de Firebase, inyectado por Spring Security tras validarlo
     * @return 200 con las recomendaciones del paciente
     */
    @GetMapping
    public ResponseEntity<List<RecomendacionDto>> getRecommendations(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(aiAlertOrchestrationService.getRecomendaciones(jwt));
    }

    /**
     * {@code GET /api/recommendations/unread}: lista las recomendaciones no leídas del paciente autenticado.
     *
     * @param jwt ID Token de Firebase, inyectado por Spring Security tras validarlo
     * @return 200 con las recomendaciones no leídas del paciente
     */
    @GetMapping("/unread")
    public ResponseEntity<List<RecomendacionDto>> getUnreadRecommendations(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(aiAlertOrchestrationService.getRecomendacionesNoLeidas(jwt));
    }

    /**
     * {@code PUT /api/recommendations/{id}/read}: marca una recomendación como leída.
     *
     * @param jwt ID Token de Firebase, inyectado por Spring Security tras validarlo
     * @param id  identificador de la recomendación
     * @return 204 sin contenido
     */
    @PutMapping("/{id}/read")
    public ResponseEntity<Void> markRecommendationAsRead(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id) {
        aiAlertOrchestrationService.marcarRecomendacionLeida(jwt, id);
        return ResponseEntity.noContent().build();
    }

    /**
     * {@code PUT /api/recommendations/read-all}: marca todas las recomendaciones del paciente autenticado como leídas.
     *
     * @param jwt ID Token de Firebase, inyectado por Spring Security tras validarlo
     * @return 204 sin contenido
     */
    @PutMapping("/read-all")
    public ResponseEntity<Void> markAllRecommendationsAsRead(@AuthenticationPrincipal Jwt jwt) {
        aiAlertOrchestrationService.marcarTodasRecomendacionesLeidas(jwt);
        return ResponseEntity.noContent().build();
    }

}
