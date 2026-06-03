package com.techleads.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class EmpresaRequest {

    @NotBlank
    @Size(max = 20)
    private String nit;

    @NotBlank
    @Size(max = 200)
    private String nombre;

    @NotBlank
    @Size(max = 300)
    private String direccion;

    @NotBlank
    @Pattern(regexp = "^[+\\d\\s\\-()]{7,20}$", message = "Teléfono inválido")
    private String telefono;
}
