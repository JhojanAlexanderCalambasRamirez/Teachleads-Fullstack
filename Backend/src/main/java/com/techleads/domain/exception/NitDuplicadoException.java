package com.techleads.domain.exception;

public class NitDuplicadoException extends RuntimeException {
    public NitDuplicadoException(String nit) {
        super("Ya existe una empresa con NIT: " + nit);
    }
}
