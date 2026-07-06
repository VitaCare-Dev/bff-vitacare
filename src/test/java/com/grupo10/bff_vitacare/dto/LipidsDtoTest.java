package com.grupo10.bff_vitacare.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class LipidsDtoTest {

    @Test
    void getterSetterEqualsHashCodeToString() {
        LocalDateTime now = LocalDateTime.now();

        LipidsDto a = new LipidsDto();
        a.setIdControl(1L);
        a.setIdPaciente(2L);
        a.setFechaHora(now);
        a.setNotas("nota");
        a.setColesterolTotal(200);
        a.setColesterolLDL(130);
        a.setColesterolHDL(40);
        a.setTrigliceridos(150);

        LipidsDto b = new LipidsDto();
        b.setIdControl(1L);
        b.setIdPaciente(2L);
        b.setFechaHora(now);
        b.setNotas("nota");
        b.setColesterolTotal(200);
        b.setColesterolLDL(130);
        b.setColesterolHDL(40);
        b.setTrigliceridos(150);

        assertThat(a.getIdControl()).isEqualTo(1L);
        assertThat(a.getIdPaciente()).isEqualTo(2L);
        assertThat(a.getFechaHora()).isEqualTo(now);
        assertThat(a.getNotas()).isEqualTo("nota");
        assertThat(a.getColesterolTotal()).isEqualTo(200);
        assertThat(a.getColesterolLDL()).isEqualTo(130);
        assertThat(a.getColesterolHDL()).isEqualTo(40);
        assertThat(a.getTrigliceridos()).isEqualTo(150);
        assertThat(a).isEqualTo(b).hasSameHashCodeAs(b);
        assertThat(a.toString()).isNotBlank();
    }
}
