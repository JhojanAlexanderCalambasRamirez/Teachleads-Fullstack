package com.techleads.domain.exception;

public class ProductoNotFoundException extends RuntimeException {
    public ProductoNotFoundException(String codigo) {
        super("Producto no encontrado con código: " + codigo);
    }
}
