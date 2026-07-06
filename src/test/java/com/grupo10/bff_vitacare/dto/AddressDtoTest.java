package com.grupo10.bff_vitacare.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class AddressDtoTest {

    @Test
    void getterSetterEqualsHashCodeToString() {
        AddressDto a = new AddressDto();
        a.setIdDireccion(1L);
        a.setIdPaciente(2L);
        a.setCalle("Av. Siempre Viva");
        a.setNumero("742");
        a.setComuna("Springfield");
        a.setRegion("RM");

        AddressDto b = new AddressDto();
        b.setIdDireccion(1L);
        b.setIdPaciente(2L);
        b.setCalle("Av. Siempre Viva");
        b.setNumero("742");
        b.setComuna("Springfield");
        b.setRegion("RM");

        assertThat(a.getIdDireccion()).isEqualTo(1L);
        assertThat(a.getIdPaciente()).isEqualTo(2L);
        assertThat(a.getCalle()).isEqualTo("Av. Siempre Viva");
        assertThat(a.getNumero()).isEqualTo("742");
        assertThat(a.getComuna()).isEqualTo("Springfield");
        assertThat(a.getRegion()).isEqualTo("RM");
        assertThat(a).isEqualTo(b).hasSameHashCodeAs(b);
        assertThat(a.toString()).isNotBlank();
    }
}
