package com.grupo10.bff_vitacare.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class RecomendacionDtoTest {

    @Test
    void getterSetterEqualsHashCodeToString() {
        LocalDateTime now = LocalDateTime.now();

        RecomendacionDto a = new RecomendacionDto();
        a.setIdRecomendacion(1L);
        a.setIdPaciente(2L);
        a.setTitulo("Reduce el sodio");
        a.setContenido("Evita alimentos procesados");
        a.setTipoRecomendacion("ALIMENTARIA");
        a.setFechaGeneracion(now);
        a.setEstadoNotificacion(true);
        a.setLeida(false);

        RecomendacionDto b = new RecomendacionDto();
        b.setIdRecomendacion(1L);
        b.setIdPaciente(2L);
        b.setTitulo("Reduce el sodio");
        b.setContenido("Evita alimentos procesados");
        b.setTipoRecomendacion("ALIMENTARIA");
        b.setFechaGeneracion(now);
        b.setEstadoNotificacion(true);
        b.setLeida(false);

        assertThat(a.getIdRecomendacion()).isEqualTo(1L);
        assertThat(a.getIdPaciente()).isEqualTo(2L);
        assertThat(a.getTitulo()).isEqualTo("Reduce el sodio");
        assertThat(a.getContenido()).isEqualTo("Evita alimentos procesados");
        assertThat(a.getTipoRecomendacion()).isEqualTo("ALIMENTARIA");
        assertThat(a.getFechaGeneracion()).isEqualTo(now);
        assertThat(a.isEstadoNotificacion()).isTrue();
        assertThat(a.isLeida()).isFalse();
        assertThat(a).isEqualTo(b).hasSameHashCodeAs(b);
        assertThat(a.toString()).isNotBlank();
    }
}
