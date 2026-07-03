package com.grupo10.bff_vitacare.dto;

import java.time.LocalDateTime;

import lombok.Data;

/**
 * Espejo de la recomendación alimentaria de IA devuelta por
 * {@code ai-alert-service} (función {@code RecomendacionAPIFunction}).
 */
@Data
public class RecomendacionDto {

    private Long idRecomendacion;

    private Long idPaciente;

    private String titulo;

    private String contenido;

    private String tipoRecomendacion;

    private LocalDateTime fechaGeneracion;

    private boolean estadoNotificacion;

    private boolean leida;

}
