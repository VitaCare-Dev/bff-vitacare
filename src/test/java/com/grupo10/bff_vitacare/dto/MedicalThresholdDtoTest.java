package com.grupo10.bff_vitacare.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class MedicalThresholdDtoTest {

    @Test
    void getterSetterEqualsHashCodeToString() {
        MedicalThresholdDto a = new MedicalThresholdDto();
        a.setIdUmbral(1L);
        a.setIdPaciente(2L);
        a.setGlucosaMax(180);
        a.setGlucosaMin(70);
        a.setSistolicaMax(140);
        a.setDiastolicaMax(90);
        a.setTemperaturaMax(38.0);

        MedicalThresholdDto b = new MedicalThresholdDto();
        b.setIdUmbral(1L);
        b.setIdPaciente(2L);
        b.setGlucosaMax(180);
        b.setGlucosaMin(70);
        b.setSistolicaMax(140);
        b.setDiastolicaMax(90);
        b.setTemperaturaMax(38.0);

        assertThat(a.getIdUmbral()).isEqualTo(1L);
        assertThat(a.getIdPaciente()).isEqualTo(2L);
        assertThat(a.getGlucosaMax()).isEqualTo(180);
        assertThat(a.getGlucosaMin()).isEqualTo(70);
        assertThat(a.getSistolicaMax()).isEqualTo(140);
        assertThat(a.getDiastolicaMax()).isEqualTo(90);
        assertThat(a.getTemperaturaMax()).isEqualTo(38.0);
        assertThat(a).isEqualTo(b).hasSameHashCodeAs(b);
        assertThat(a.toString()).isNotBlank();
    }
}
