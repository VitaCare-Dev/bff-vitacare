package com.grupo10.bff_vitacare.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class UpdatePatientRequestDtoTest {

    @Test
    void getterSetterEqualsHashCodeToString() {
        LocalDate birth = LocalDate.of(1990, 5, 15);

        UpdatePatientRequestDto a = new UpdatePatientRequestDto();
        a.setNombre("María José");
        a.setApellidoPaterno("Pérez");
        a.setApellidoMaterno("Soto");
        a.setFechaNacimiento(birth);
        a.setTelefonoPrincipal("+56912345678");
        a.setTelefonoSecundario(null);

        UpdatePatientRequestDto b = new UpdatePatientRequestDto();
        b.setNombre("María José");
        b.setApellidoPaterno("Pérez");
        b.setApellidoMaterno("Soto");
        b.setFechaNacimiento(birth);
        b.setTelefonoPrincipal("+56912345678");
        b.setTelefonoSecundario(null);

        assertThat(a.getNombre()).isEqualTo("María José");
        assertThat(a.getApellidoPaterno()).isEqualTo("Pérez");
        assertThat(a.getApellidoMaterno()).isEqualTo("Soto");
        assertThat(a.getFechaNacimiento()).isEqualTo(birth);
        assertThat(a.getTelefonoPrincipal()).isEqualTo("+56912345678");
        assertThat(a.getTelefonoSecundario()).isNull();
        assertThat(a).isEqualTo(b).hasSameHashCodeAs(b);
        assertThat(a.toString()).isNotBlank();
    }
}
