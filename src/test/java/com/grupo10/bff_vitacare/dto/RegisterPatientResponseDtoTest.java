package com.grupo10.bff_vitacare.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class RegisterPatientResponseDtoTest {

    @Test
    void getterSetterEqualsHashCodeToString() {
        LocalDate birth = LocalDate.of(1990, 5, 15);

        RegisterPatientResponseDto a = new RegisterPatientResponseDto();
        a.setIdUsuario(1L);
        a.setCorreo("a@b.cl");
        a.setRol("PACIENTE");
        a.setIdPaciente(2L);
        a.setRut("12.345.678-9");
        a.setNombre("María");
        a.setApellidoPaterno("Pérez");
        a.setApellidoMaterno("Soto");
        a.setFechaNacimiento(birth);
        a.setTelefonoPrincipal("+56912345678");
        a.setTelefonoSecundario(null);

        RegisterPatientResponseDto b = new RegisterPatientResponseDto();
        b.setIdUsuario(1L);
        b.setCorreo("a@b.cl");
        b.setRol("PACIENTE");
        b.setIdPaciente(2L);
        b.setRut("12.345.678-9");
        b.setNombre("María");
        b.setApellidoPaterno("Pérez");
        b.setApellidoMaterno("Soto");
        b.setFechaNacimiento(birth);
        b.setTelefonoPrincipal("+56912345678");
        b.setTelefonoSecundario(null);

        assertThat(a.getIdUsuario()).isEqualTo(1L);
        assertThat(a.getCorreo()).isEqualTo("a@b.cl");
        assertThat(a.getRol()).isEqualTo("PACIENTE");
        assertThat(a.getIdPaciente()).isEqualTo(2L);
        assertThat(a.getRut()).isEqualTo("12.345.678-9");
        assertThat(a.getNombre()).isEqualTo("María");
        assertThat(a.getApellidoPaterno()).isEqualTo("Pérez");
        assertThat(a.getApellidoMaterno()).isEqualTo("Soto");
        assertThat(a.getFechaNacimiento()).isEqualTo(birth);
        assertThat(a.getTelefonoPrincipal()).isEqualTo("+56912345678");
        assertThat(a.getTelefonoSecundario()).isNull();
        assertThat(a).isEqualTo(b).hasSameHashCodeAs(b);
        assertThat(a.toString()).isNotBlank();
    }
}
