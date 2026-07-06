package com.grupo10.bff_vitacare.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class RegistrationConflictExceptionTest {

    @Test
    void carriesTheGivenMessage() {
        RegistrationConflictException ex = new RegistrationConflictException("conflict");
        assertThat(ex.getMessage()).isEqualTo("conflict");
    }
}
