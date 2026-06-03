package com.techleads.application.dto.response;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class EmpresaResponse {
    String nit;
    String nombre;
    String direccion;
    String telefono;
}
