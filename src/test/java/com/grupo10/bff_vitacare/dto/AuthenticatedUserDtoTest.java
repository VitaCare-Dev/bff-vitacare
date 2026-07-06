package com.grupo10.bff_vitacare.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class AuthenticatedUserDtoTest {

    @Test
    void getterSetterEqualsHashCodeToString() {
        LocalDateTime now = LocalDateTime.now();

        AuthenticatedUserDto a = new AuthenticatedUserDto();
        a.setId(1L);
        a.setCorreo("a@b.cl");
        a.setRol("PACIENTE");
        a.setActivo(1);
        a.setFirebaseUid("uid-1");
        a.setCreatedAt(now);

        AuthenticatedUserDto b = new AuthenticatedUserDto();
        b.setId(1L);
        b.setCorreo("a@b.cl");
        b.setRol("PACIENTE");
        b.setActivo(1);
        b.setFirebaseUid("uid-1");
        b.setCreatedAt(now);

        assertThat(a.getId()).isEqualTo(1L);
        assertThat(a.getCorreo()).isEqualTo("a@b.cl");
        assertThat(a.getRol()).isEqualTo("PACIENTE");
        assertThat(a.getActivo()).isEqualTo(1);
        assertThat(a.getFirebaseUid()).isEqualTo("uid-1");
        assertThat(a.getCreatedAt()).isEqualTo(now);
        assertThat(a).isEqualTo(b).hasSameHashCodeAs(b);
        assertThat(a.toString()).isNotBlank();
    }
}
