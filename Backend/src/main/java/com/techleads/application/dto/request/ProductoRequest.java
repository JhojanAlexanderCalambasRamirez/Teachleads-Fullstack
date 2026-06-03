package com.techleads.application.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class ProductoRequest {

    @NotBlank
    @Size(max = 50)
    private String codigo;

    @NotBlank
    @Size(max = 200)
    private String nombre;

    @Size(max = 1000)
    private String caracteristicas;

    @NotBlank
    private String empresaNit;

    @NotEmpty
    @Valid
    private List<ProductoPrecioRequest> precios;

    private List<Long> categoriaIds;
}
