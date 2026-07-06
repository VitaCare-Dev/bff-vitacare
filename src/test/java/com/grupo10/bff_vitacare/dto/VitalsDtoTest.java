package com.grupo10.bff_vitacare.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class VitalsDtoTest {

    @Test
    void getterSetterEqualsHashCodeToString() {
        LocalDateTime now = LocalDateTime.now();

        VitalsDto a = new VitalsDto();
        a.setIdControl(1L);
        a.setIdPaciente(2L);
        a.setFechaHora(now);
        a.setNotas("nota");
        a.setPresionSistolica(120);
        a.setPresionDiastolica(80);
        a.setTemperatura(36.6);
        a.setPeso(65.0);

        VitalsDto b = new VitalsDto();
        b.setIdControl(1L);
        b.setIdPaciente(2L);
        b.setFechaHora(now);
        b.setNotas("nota");
        b.setPresionSistolica(120);
        b.setPresionDiastolica(80);
        b.setTemperatura(36.6);
        b.setPeso(65.0);

        assertThat(a.getIdControl()).isEqualTo(1L);
        assertThat(a.getIdPaciente()).isEqualTo(2L);
        assertThat(a.getFechaHora()).isEqualTo(now);
        assertThat(a.getNotas()).isEqualTo("nota");
        assertThat(a.getPresionSistolica()).isEqualTo(120);
        assertThat(a.getPresionDiastolica()).isEqualTo(80);
        assertThat(a.getTemperatura()).isEqualTo(36.6);
        assertThat(a.getPeso()).isEqualTo(65.0);
        assertThat(a).isEqualTo(b).hasSameHashCodeAs(b);
        assertThat(a.toString()).isNotBlank();
    }

    @Test
    void allowsNullBloodPressure() {
        VitalsDto vitals = new VitalsDto();
        vitals.setPresionSistolica(null);
        vitals.setPresionDiastolica(null);

        assertThat(vitals.getPresionSistolica()).isNull();
        assertThat(vitals.getPresionDiastolica()).isNull();
    }
}
