package com.techleads.domain.model;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder(toBuilder = true)
public class Producto {
    String codigo;
    String nombre;
    String caracteristicas;
    String empresaNit;
    List<ProductoPrecio> precios;
    List<Categoria> categorias;
}
