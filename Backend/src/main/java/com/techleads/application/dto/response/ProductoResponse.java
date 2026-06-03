package com.techleads.application.dto.response;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class ProductoResponse {
    String codigo;
    String nombre;
    String caracteristicas;
    String empresaNit;
    String empresaNombre;
    List<ProductoPrecioResponse> precios;
    List<CategoriaResponse> categorias;
}
