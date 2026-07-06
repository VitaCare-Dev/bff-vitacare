package com.grupo10.bff_vitacare.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class AddressRequestDtoTest {

    @Test
    void getterSetterEqualsHashCodeToString() {
        AddressRequestDto a = new AddressRequestDto();
        a.setCalle("Av. Siempre Viva");
        a.setNumero("742");
        a.setComuna("Springfield");
        a.setRegion("RM");

        AddressRequestDto b = new AddressRequestDto();
        b.setCalle("Av. Siempre Viva");
        b.setNumero("742");
        b.setComuna("Springfield");
        b.setRegion("RM");

        assertThat(a.getCalle()).isEqualTo("Av. Siempre Viva");
        assertThat(a.getNumero()).isEqualTo("742");
        assertThat(a.getComuna()).isEqualTo("Springfield");
        assertThat(a.getRegion()).isEqualTo("RM");
        assertThat(a).isEqualTo(b).hasSameHashCodeAs(b);
        assertThat(a.toString()).isNotBlank();
    }
}
