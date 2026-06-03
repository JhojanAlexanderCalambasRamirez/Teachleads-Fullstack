package com.techleads.application.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class InventarioRequest {

    @NotBlank
    private String empresaNit;

    @NotBlank
    private String productoCodigo;

    @NotNull
    @Min(0)
    private Integer cantidad;
}
