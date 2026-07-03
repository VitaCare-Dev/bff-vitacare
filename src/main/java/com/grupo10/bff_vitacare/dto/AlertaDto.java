package com.grupo10.bff_vitacare.dto;

import java.time.LocalDateTime;

import lombok.Data;

/**
 * Espejo de la alerta de IA devuelta por {@code ai-alert-service}
 * (función {@code AlertaAPIFunction}).
 */
@Data
public class AlertaDto {

    private Long idAlertaIa;

    private Long idPaciente;

    private LocalDateTime fechaDisparo;

    private String motivoAlerta;

    private String recomendacionIa;

    private boolean leida;

}
