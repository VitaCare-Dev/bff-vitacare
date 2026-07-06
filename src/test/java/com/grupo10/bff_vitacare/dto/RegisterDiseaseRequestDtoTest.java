package com.grupo10.bff_vitacare.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class RegisterDiseaseRequestDtoTest {

    @Test
    void getterSetterEqualsHashCodeToString() {
        RegisterDiseaseRequestDto a = new RegisterDiseaseRequestDto();
        a.setIdEnfermedad(1L);

        RegisterDiseaseRequestDto b = new RegisterDiseaseRequestDto();
        b.setIdEnfermedad(1L);

        assertThat(a.getIdEnfermedad()).isEqualTo(1L);
        assertThat(a).isEqualTo(b).hasSameHashCodeAs(b);
        assertThat(a.toString()).isNotBlank();
    }
}
