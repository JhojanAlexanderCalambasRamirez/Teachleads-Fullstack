package com.techleads.domain.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Empresa {
    String nit;
    String nombre;
    String direccion;
    String telefono;
}
