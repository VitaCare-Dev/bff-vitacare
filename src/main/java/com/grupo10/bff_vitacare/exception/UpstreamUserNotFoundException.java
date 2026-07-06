package com.grupo10.bff_vitacare.exception;

/**
 * Excepción lanzada cuando el usuario autenticado en Firebase aún no fue
 * sincronizado con {@code tb_usuario} (no existe en {@code user-service}).
 */
public class UpstreamUserNotFoundException extends RuntimeException {

    /**
     * @param message detalle del error, propagado tal cual al cliente
     */
    public UpstreamUserNotFoundException(String message) {
        super(message);
    }

}
