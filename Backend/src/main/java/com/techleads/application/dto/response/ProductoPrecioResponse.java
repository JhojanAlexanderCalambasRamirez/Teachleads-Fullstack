package com.techleads.application.dto.response;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
public class ProductoPrecioResponse {
    Long id;
    String moneda;
    BigDecimal precio;
}
