package com.techleads.application.dto.response;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CategoriaResponse {
    Long id;
    String nombre;
}
