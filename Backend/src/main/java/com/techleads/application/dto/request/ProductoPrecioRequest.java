package com.techleads.application.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductoPrecioRequest {

    @NotBlank
    @Size(min = 3, max = 3)
    private String moneda;

    @NotNull
    @DecimalMin("0.01")
    private BigDecimal precio;
}
