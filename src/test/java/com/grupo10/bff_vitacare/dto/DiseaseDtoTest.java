package com.grupo10.bff_vitacare.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class DiseaseDtoTest {

    @Test
    void getterSetterEqualsHashCodeToString() {
        DiseaseDto a = new DiseaseDto();
        a.setIdEnfermedad(1L);
        a.setNombreEnfermedad("Diabetes tipo 2");
        a.setDescripcion("Descripción");

        DiseaseDto b = new DiseaseDto();
        b.setIdEnfermedad(1L);
        b.setNombreEnfermedad("Diabetes tipo 2");
        b.setDescripcion("Descripción");

        assertThat(a.getIdEnfermedad()).isEqualTo(1L);
        assertThat(a.getNombreEnfermedad()).isEqualTo("Diabetes tipo 2");
        assertThat(a.getDescripcion()).isEqualTo("Descripción");
        assertThat(a).isEqualTo(b).hasSameHashCodeAs(b);
        assertThat(a.toString()).isNotBlank();
    }
}
