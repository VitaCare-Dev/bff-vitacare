package com.grupo10.bff_vitacare.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class UpstreamErrorExceptionTest {

    @Test
    void carriesTheGivenStatusAndMessage() {
        UpstreamErrorException ex = new UpstreamErrorException(HttpStatus.BAD_GATEWAY, "boom");

        assertThat(ex.getMessage()).isEqualTo("boom");
        assertThat(ex.getStatus()).isEqualTo(HttpStatus.BAD_GATEWAY);
    }
}
