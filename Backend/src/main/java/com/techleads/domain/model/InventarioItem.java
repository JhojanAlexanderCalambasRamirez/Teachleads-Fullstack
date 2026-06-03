package com.techleads.domain.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class InventarioItem {
    Long id;
    String empresaNit;
    String empresaNombre;
    String productoCodigo;
    String productoNombre;
    String productoCaracteristicas;
    Integer cantidad;
}
