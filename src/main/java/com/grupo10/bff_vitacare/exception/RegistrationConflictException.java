package com.grupo10.bff_vitacare.exception;

/**
 * Excepción lanzada cuando un microservicio de dominio rechaza el registro
 * por un conflicto de datos (ej. correo o RUT duplicado).
 */
public class RegistrationConflictException extends RuntimeException {

    /**
     * @param message detalle del conflicto, propagado tal cual al cliente
     */
    public RegistrationConflictException(String message) {
        super(message);
    }

}
