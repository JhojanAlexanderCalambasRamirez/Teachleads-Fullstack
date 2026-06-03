package com.techleads.domain.port;

import com.techleads.domain.model.Producto;

import java.util.List;
import java.util.Optional;

public interface ProductoRepository {
    Producto save(Producto producto);
    Optional<Producto> findByCodigo(String codigo);
    List<Producto> findAll();
    List<Producto> findByEmpresaNit(String empresaNit);
    void deleteByCodigo(String codigo);
    boolean existsByCodigo(String codigo);
}
