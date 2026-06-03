package com.techleads.domain.port;

import com.techleads.domain.model.Orden;

import java.util.List;
import java.util.Optional;

public interface OrdenRepository {
    Orden save(Orden orden);
    Optional<Orden> findById(Long id);
    List<Orden> findAll();
    List<Orden> findByClienteId(Long clienteId);
    void deleteById(Long id);
}
