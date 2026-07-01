package com.grupo10.bff_vitacare.dto;

import java.time.LocalDateTime;
import lombok.Data;

/**
 * Datos del usuario autenticado, resueltos a partir del UID de Firebase
 * presente en el ID Token. Es el objeto que las rutas de orquestación del
 * BFF usan para saber quién hace la llamada, sin volver a consultar Firebase.
 */
@Data
public class AuthenticatedUserDto {

    /** Identificador interno del usuario en {@code tb_usuario} (id_usuario). */
    private Long id;

    private String correo;

    private String rol;

    private int activo;

    private String firebaseUid;

    private LocalDateTime createdAt;

}
