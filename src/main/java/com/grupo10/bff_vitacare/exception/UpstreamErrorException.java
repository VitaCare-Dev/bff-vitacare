package com.grupo10.bff_vitacare.exception;

import org.springframework.http.HttpStatusCode;

/**
 * Excepción genérica que envuelve un error 4xx devuelto por un microservicio
 * de dominio, preservando su código de estado para reenviarlo tal cual al
 * cliente del BFF.
 */
public class UpstreamErrorException extends RuntimeException {

    private final HttpStatusCode status;

    /**
     * @param status  código de estado HTTP devuelto por el microservicio ascendente
     * @param message detalle del error, propagado tal cual al cliente
     */
    public UpstreamErrorException(HttpStatusCode status, String message) {
        super(message);
        this.status = status;
    }

    /**
     * @return el código de estado HTTP original devuelto por el microservicio ascendente
     */
    public HttpStatusCode getStatus() {
        return status;
    }

}
