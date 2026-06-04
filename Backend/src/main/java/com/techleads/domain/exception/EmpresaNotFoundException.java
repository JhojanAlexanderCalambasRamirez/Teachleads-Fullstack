package com.techleads.domain.exception;

public class EmpresaNotFoundException extends RuntimeException {
    public EmpresaNotFoundException(String nit) {
        super("Empresa no encontrada con NIT: " + nit);
    }
}
