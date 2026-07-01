package com.grupo10.bff_vitacare.exception;

/**
 * Excepción lanzada cuando el usuario autenticado todavía no completó el
 * registro de paciente ({@code POST /api/auth/register}).
 */
public class PatientNotFoundException extends RuntimeException {

    public PatientNotFoundException(String message) {
        super(message);
    }

}
