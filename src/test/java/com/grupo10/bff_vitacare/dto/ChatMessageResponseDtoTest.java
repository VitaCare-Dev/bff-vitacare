package com.grupo10.bff_vitacare.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ChatMessageResponseDtoTest {

    @Test
    void getterSetterEqualsHashCodeToString() {
        ChatMessageResponseDto a = new ChatMessageResponseDto();
        a.setRespuesta("hola, ¿en qué puedo ayudarte?");

        ChatMessageResponseDto b = new ChatMessageResponseDto();
        b.setRespuesta("hola, ¿en qué puedo ayudarte?");

        assertThat(a.getRespuesta()).isEqualTo("hola, ¿en qué puedo ayudarte?");
        assertThat(a).isEqualTo(b).hasSameHashCodeAs(b);
        assertThat(a.toString()).isNotBlank();
    }
}
