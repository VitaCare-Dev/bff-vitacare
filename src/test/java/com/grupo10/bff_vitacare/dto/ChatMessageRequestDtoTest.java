package com.grupo10.bff_vitacare.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ChatMessageRequestDtoTest {

    @Test
    void getterSetterEqualsHashCodeToString() {
        ChatMessageRequestDto a = new ChatMessageRequestDto();
        a.setMensaje("hola");

        ChatMessageRequestDto b = new ChatMessageRequestDto();
        b.setMensaje("hola");

        assertThat(a.getMensaje()).isEqualTo("hola");
        assertThat(a).isEqualTo(b).hasSameHashCodeAs(b);
        assertThat(a.toString()).isNotBlank();
    }
}
