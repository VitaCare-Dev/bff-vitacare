package com.grupo10.bff_vitacare.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class ErrorResponseDtoTest {

    @Test
    void getterSetterEqualsHashCodeToString() {
        LocalDateTime now = LocalDateTime.now();

        ErrorResponseDto a = new ErrorResponseDto();
        a.setMessage("error");
        a.setStatus(404);
        a.setTimestamp(now);

        ErrorResponseDto b = new ErrorResponseDto();
        b.setMessage("error");
        b.setStatus(404);
        b.setTimestamp(now);

        assertThat(a.getMessage()).isEqualTo("error");
        assertThat(a.getStatus()).isEqualTo(404);
        assertThat(a.getTimestamp()).isEqualTo(now);
        assertThat(a).isEqualTo(b).hasSameHashCodeAs(b);
        assertThat(a.toString()).isNotBlank();
    }
}
