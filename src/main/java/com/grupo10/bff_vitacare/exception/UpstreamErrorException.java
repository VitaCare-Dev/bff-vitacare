package com.grupo10.bff_vitacare.exception;

import org.springframework.http.HttpStatusCode;

/**
 * Excepción genérica que envuelve un error 4xx devuelto por un microservicio
 * de dominio, preservando su código de estado para reenviarlo tal cual al
 * cliente del BFF.
 */
public class UpstreamErrorException extends RuntimeException {

    private final HttpStatusCode status;

    public UpstreamErrorException(HttpStatusCode status, String message) {
        super(message);
        this.status = status;
    }

    public HttpStatusCode getStatus() {
        return status;
    }

}
