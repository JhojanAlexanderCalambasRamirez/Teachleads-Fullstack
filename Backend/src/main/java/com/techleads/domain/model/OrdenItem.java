package com.techleads.domain.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class OrdenItem {
    String productoCodigo;
    String productoNombre;
    Integer cantidad;
}
