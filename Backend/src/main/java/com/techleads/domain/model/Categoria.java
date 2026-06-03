package com.techleads.domain.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Categoria {
    Long id;
    String nombre;
}
