package com.grupo10.bff_vitacare.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class MedicationDtoTest {

    @Test
    void getterSetterEqualsHashCodeToString() {
        LocalDate start = LocalDate.of(2026, 1, 1);
        LocalDate end = LocalDate.of(2026, 6, 1);

        MedicationDto a = new MedicationDto();
        a.setIdMedicamento(1L);
        a.setIdPaciente(2L);
        a.setNombreMedicamento("Metformina");
        a.setDosis("850 mg");
        a.setFrecuenciaHoras(12);
        a.setFechaInicio(start);
        a.setFechaTermino(end);
        a.setActivo(1);

        MedicationDto b = new MedicationDto();
        b.setIdMedicamento(1L);
        b.setIdPaciente(2L);
        b.setNombreMedicamento("Metformina");
        b.setDosis("850 mg");
        b.setFrecuenciaHoras(12);
        b.setFechaInicio(start);
        b.setFechaTermino(end);
        b.setActivo(1);

        assertThat(a.getIdMedicamento()).isEqualTo(1L);
        assertThat(a.getIdPaciente()).isEqualTo(2L);
        assertThat(a.getNombreMedicamento()).isEqualTo("Metformina");
        assertThat(a.getDosis()).isEqualTo("850 mg");
        assertThat(a.getFrecuenciaHoras()).isEqualTo(12);
        assertThat(a.getFechaInicio()).isEqualTo(start);
        assertThat(a.getFechaTermino()).isEqualTo(end);
        assertThat(a.getActivo()).isEqualTo(1);
        assertThat(a).isEqualTo(b).hasSameHashCodeAs(b);
        assertThat(a.toString()).isNotBlank();
    }
}
