package com.techleads.domain.exception;

public class InventarioItemNotFoundException extends RuntimeException {
    public InventarioItemNotFoundException(Long id) {
        super("Item de inventario no encontrado con ID: " + id);
    }
}
