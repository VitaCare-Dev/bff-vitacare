package com.grupo10.bff_vitacare.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class HealthControlDtoTest {

    @Test
    void getterSetterEqualsHashCodeToString() {
        LocalDateTime now = LocalDateTime.now();

        HealthControlDto a = new HealthControlDto();
        a.setIdControl(1L);
        a.setIdPaciente(2L);
        a.setFechaHora(now);
        a.setNotas("nota");

        HealthControlDto b = new HealthControlDto();
        b.setIdControl(1L);
        b.setIdPaciente(2L);
        b.setFechaHora(now);
        b.setNotas("nota");

        assertThat(a.getIdControl()).isEqualTo(1L);
        assertThat(a.getIdPaciente()).isEqualTo(2L);
        assertThat(a.getFechaHora()).isEqualTo(now);
        assertThat(a.getNotas()).isEqualTo("nota");
        assertThat(a).isEqualTo(b).hasSameHashCodeAs(b);
        assertThat(a.toString()).isNotBlank();
    }
}
