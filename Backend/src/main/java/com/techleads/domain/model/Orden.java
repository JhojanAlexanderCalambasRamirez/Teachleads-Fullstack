package com.techleads.domain.model;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.List;

@Value
@Builder
public class Orden {
    Long id;
    Long clienteId;
    LocalDateTime fecha;
    String estado;
    List<OrdenItem> items;
}
