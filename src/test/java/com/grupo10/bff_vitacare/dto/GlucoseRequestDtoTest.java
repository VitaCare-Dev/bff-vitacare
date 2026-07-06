package com.grupo10.bff_vitacare.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class GlucoseRequestDtoTest {

    @Test
    void getterSetterEqualsHashCodeToString() {
        GlucoseRequestDto a = new GlucoseRequestDto();
        a.setNotas("en ayunas");
        a.setGlucosa(98);
        a.setPeriodo("AYUNAS");

        GlucoseRequestDto b = new GlucoseRequestDto();
        b.setNotas("en ayunas");
        b.setGlucosa(98);
        b.setPeriodo("AYUNAS");

        assertThat(a.getNotas()).isEqualTo("en ayunas");
        assertThat(a.getGlucosa()).isEqualTo(98);
        assertThat(a.getPeriodo()).isEqualTo("AYUNAS");
        assertThat(a).isEqualTo(b).hasSameHashCodeAs(b);
        assertThat(a.toString()).isNotBlank();
    }
}
