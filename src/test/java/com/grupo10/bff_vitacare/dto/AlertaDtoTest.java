package com.grupo10.bff_vitacare.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class AlertaDtoTest {

    @Test
    void getterSetterEqualsHashCodeToString() {
        LocalDateTime now = LocalDateTime.now();

        AlertaDto a = new AlertaDto();
        a.setIdAlertaIa(1L);
        a.setIdPaciente(2L);
        a.setFechaDisparo(now);
        a.setMotivoAlerta("glucosa alta");
        a.setRecomendacionIa("consulta a tu médico");
        a.setLeida(true);

        AlertaDto b = new AlertaDto();
        b.setIdAlertaIa(1L);
        b.setIdPaciente(2L);
        b.setFechaDisparo(now);
        b.setMotivoAlerta("glucosa alta");
        b.setRecomendacionIa("consulta a tu médico");
        b.setLeida(true);

        assertThat(a.getIdAlertaIa()).isEqualTo(1L);
        assertThat(a.getIdPaciente()).isEqualTo(2L);
        assertThat(a.getFechaDisparo()).isEqualTo(now);
        assertThat(a.getMotivoAlerta()).isEqualTo("glucosa alta");
        assertThat(a.getRecomendacionIa()).isEqualTo("consulta a tu médico");
        assertThat(a.isLeida()).isTrue();
        assertThat(a).isEqualTo(b).hasSameHashCodeAs(b);
        assertThat(a.toString()).isNotBlank();
    }
}
