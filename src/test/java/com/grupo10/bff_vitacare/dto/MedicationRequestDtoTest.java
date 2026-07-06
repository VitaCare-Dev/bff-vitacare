package com.grupo10.bff_vitacare.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class MedicationRequestDtoTest {

    @Test
    void getterSetterEqualsHashCodeToString() {
        LocalDate start = LocalDate.of(2026, 1, 1);
        LocalDate end = LocalDate.of(2026, 6, 1);

        MedicationRequestDto a = new MedicationRequestDto();
        a.setNombreMedicamento("Metformina");
        a.setDosis("850 mg");
        a.setFrecuenciaHoras(12);
        a.setFechaInicio(start);
        a.setFechaTermino(end);

        MedicationRequestDto b = new MedicationRequestDto();
        b.setNombreMedicamento("Metformina");
        b.setDosis("850 mg");
        b.setFrecuenciaHoras(12);
        b.setFechaInicio(start);
        b.setFechaTermino(end);

        assertThat(a.getNombreMedicamento()).isEqualTo("Metformina");
        assertThat(a.getDosis()).isEqualTo("850 mg");
        assertThat(a.getFrecuenciaHoras()).isEqualTo(12);
        assertThat(a.getFechaInicio()).isEqualTo(start);
        assertThat(a.getFechaTermino()).isEqualTo(end);
        assertThat(a).isEqualTo(b).hasSameHashCodeAs(b);
        assertThat(a.toString()).isNotBlank();
    }
}
