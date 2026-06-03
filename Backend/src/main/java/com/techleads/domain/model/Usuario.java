package com.techleads.domain.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Usuario {
    Long id;
    String correo;
    String passwordHash;
    String nombre;
    Rol rol;
    boolean activo;

    public enum Rol {
        ADMIN, EXTERNO
    }
}
