package com.grupo10.bff_vitacare.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class VitalsRequestDtoTest {

    @Test
    void getterSetterEqualsHashCodeToString() {
        VitalsRequestDto a = new VitalsRequestDto();
        a.setNotas("nota");
        a.setPresionSistolica(120);
        a.setPresionDiastolica(80);
        a.setTemperatura(36.6);
        a.setPeso(65.0);

        VitalsRequestDto b = new VitalsRequestDto();
        b.setNotas("nota");
        b.setPresionSistolica(120);
        b.setPresionDiastolica(80);
        b.setTemperatura(36.6);
        b.setPeso(65.0);

        assertThat(a.getNotas()).isEqualTo("nota");
        assertThat(a.getPresionSistolica()).isEqualTo(120);
        assertThat(a.getPresionDiastolica()).isEqualTo(80);
        assertThat(a.getTemperatura()).isEqualTo(36.6);
        assertThat(a.getPeso()).isEqualTo(65.0);
        assertThat(a).isEqualTo(b).hasSameHashCodeAs(b);
        assertThat(a.toString()).isNotBlank();
    }
}
