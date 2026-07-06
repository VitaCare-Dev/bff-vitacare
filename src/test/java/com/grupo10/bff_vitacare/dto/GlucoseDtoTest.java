package com.grupo10.bff_vitacare.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class GlucoseDtoTest {

    @Test
    void getterSetterEqualsHashCodeToString() {
        LocalDateTime now = LocalDateTime.now();

        GlucoseDto a = new GlucoseDto();
        a.setIdControl(1L);
        a.setIdPaciente(2L);
        a.setFechaHora(now);
        a.setNotas("en ayunas");
        a.setGlucosa(98);
        a.setPeriodo("AYUNAS");

        GlucoseDto b = new GlucoseDto();
        b.setIdControl(1L);
        b.setIdPaciente(2L);
        b.setFechaHora(now);
        b.setNotas("en ayunas");
        b.setGlucosa(98);
        b.setPeriodo("AYUNAS");

        assertThat(a.getIdControl()).isEqualTo(1L);
        assertThat(a.getIdPaciente()).isEqualTo(2L);
        assertThat(a.getFechaHora()).isEqualTo(now);
        assertThat(a.getNotas()).isEqualTo("en ayunas");
        assertThat(a.getGlucosa()).isEqualTo(98);
        assertThat(a.getPeriodo()).isEqualTo("AYUNAS");
        assertThat(a).isEqualTo(b).hasSameHashCodeAs(b);
        assertThat(a.toString()).isNotBlank();
    }
}
