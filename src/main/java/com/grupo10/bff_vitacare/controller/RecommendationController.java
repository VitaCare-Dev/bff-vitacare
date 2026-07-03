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

    public RecommendationController(AiAlertOrchestrationService aiAlertOrchestrationService) {
        this.aiAlertOrchestrationService = aiAlertOrchestrationService;
    }

    @GetMapping
    public ResponseEntity<List<RecomendacionDto>> getRecommendations(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(aiAlertOrchestrationService.getRecomendaciones(jwt));
    }

    @GetMapping("/unread")
    public ResponseEntity<List<RecomendacionDto>> getUnreadRecommendations(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(aiAlertOrchestrationService.getRecomendacionesNoLeidas(jwt));
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<Void> markRecommendationAsRead(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id) {
        aiAlertOrchestrationService.marcarRecomendacionLeida(jwt, id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/read-all")
    public ResponseEntity<Void> markAllRecommendationsAsRead(@AuthenticationPrincipal Jwt jwt) {
        aiAlertOrchestrationService.marcarTodasRecomendacionesLeidas(jwt);
        return ResponseEntity.noContent().build();
    }

}
