package com.grupo10.bff_vitacare.dto;

import lombok.Data;

/**
 * Mensaje enviado por el paciente autenticado al chatbot IA. El
 * {@code idUsuario} no viaja aquí: se resuelve del token.
 */
@Data
public class ChatMessageRequestDto {

    private String mensaje;

}
