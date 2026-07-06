package com.grupo10.bff_vitacare.exception;

import static org.assertj.core.api.Assertions.assertThat;

import com.grupo10.bff_vitacare.dto.ErrorResponseDto;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handlesUpstreamUserNotFound() {
        ResponseEntity<ErrorResponseDto> response =
                handler.handleUpstreamUserNotFound(new UpstreamUserNotFoundException("no user"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().getMessage()).isEqualTo("no user");
        assertThat(response.getBody().getStatus()).isEqualTo(404);
        assertThat(response.getBody().getTimestamp()).isNotNull();
    }

    @Test
    void handlesRegistrationConflict() {
        ResponseEntity<ErrorResponseDto> response =
                handler.handleRegistrationConflict(new RegistrationConflictException("conflict"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody().getMessage()).isEqualTo("conflict");
        assertThat(response.getBody().getStatus()).isEqualTo(409);
    }

    @Test
    void handlesPatientNotFound() {
        ResponseEntity<ErrorResponseDto> response =
                handler.handlePatientNotFound(new PatientNotFoundException("no patient"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().getMessage()).isEqualTo("no patient");
        assertThat(response.getBody().getStatus()).isEqualTo(404);
    }

    @Test
    void handlesUpstreamErrorPreservingItsStatus() {
        ResponseEntity<ErrorResponseDto> response =
                handler.handleUpstreamError(new UpstreamErrorException(HttpStatus.BAD_GATEWAY, "upstream down"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_GATEWAY);
        assertThat(response.getBody().getMessage()).isEqualTo("upstream down");
        assertThat(response.getBody().getStatus()).isEqualTo(502);
    }
}
