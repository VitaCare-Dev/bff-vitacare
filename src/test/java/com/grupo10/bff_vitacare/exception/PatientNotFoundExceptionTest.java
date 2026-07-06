package com.grupo10.bff_vitacare.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class PatientNotFoundExceptionTest {

    @Test
    void carriesTheGivenMessage() {
        PatientNotFoundException ex = new PatientNotFoundException("no patient");
        assertThat(ex.getMessage()).isEqualTo("no patient");
    }
}
