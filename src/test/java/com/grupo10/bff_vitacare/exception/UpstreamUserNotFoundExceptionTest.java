package com.grupo10.bff_vitacare.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class UpstreamUserNotFoundExceptionTest {

    @Test
    void carriesTheGivenMessage() {
        UpstreamUserNotFoundException ex = new UpstreamUserNotFoundException("not synced");
        assertThat(ex.getMessage()).isEqualTo("not synced");
    }
}
