package com.techleads.domain.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Cliente {
    Long id;
    String nombre;
    String correo;
}
