package com.techleads.domain.exception;

public class ProductoCodigoDuplicadoException extends RuntimeException {
    public ProductoCodigoDuplicadoException(String codigo) {
        super("Ya existe un producto con código: " + codigo);
    }
}
