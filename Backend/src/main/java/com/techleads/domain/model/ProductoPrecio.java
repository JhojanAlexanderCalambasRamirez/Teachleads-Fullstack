package com.techleads.domain.model;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
public class ProductoPrecio {
    Long id;
    String moneda;
    BigDecimal precio;
}
