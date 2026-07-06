package com.grupo10.bff_vitacare.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class LipidsRequestDtoTest {

    @Test
    void getterSetterEqualsHashCodeToString() {
        LipidsRequestDto a = new LipidsRequestDto();
        a.setNotas("nota");
        a.setColesterolTotal(200);
        a.setColesterolLDL(130);
        a.setColesterolHDL(40);
        a.setTrigliceridos(150);

        LipidsRequestDto b = new LipidsRequestDto();
        b.setNotas("nota");
        b.setColesterolTotal(200);
        b.setColesterolLDL(130);
        b.setColesterolHDL(40);
        b.setTrigliceridos(150);

        assertThat(a.getNotas()).isEqualTo("nota");
        assertThat(a.getColesterolTotal()).isEqualTo(200);
        assertThat(a.getColesterolLDL()).isEqualTo(130);
        assertThat(a.getColesterolHDL()).isEqualTo(40);
        assertThat(a.getTrigliceridos()).isEqualTo(150);
        assertThat(a).isEqualTo(b).hasSameHashCodeAs(b);
        assertThat(a.toString()).isNotBlank();
    }
}
