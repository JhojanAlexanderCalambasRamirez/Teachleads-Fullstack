package com.techleads.application.dto.response;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class InventarioResponse {
    Long id;
    String empresaNit;
    String empresaNombre;
    String productoCodigo;
    String productoNombre;
    String productoCaracteristicas;
    Integer cantidad;
}
