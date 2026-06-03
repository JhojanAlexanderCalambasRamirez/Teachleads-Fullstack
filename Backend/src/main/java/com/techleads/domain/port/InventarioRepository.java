package com.techleads.domain.port;

import com.techleads.domain.model.InventarioItem;

import java.util.List;
import java.util.Optional;

public interface InventarioRepository {
    InventarioItem save(InventarioItem item);
    List<InventarioItem> findAll();
    List<InventarioItem> findByEmpresaNit(String empresaNit);
    Optional<InventarioItem> findByEmpresaNitAndProductoCodigo(String empresaNit, String productoCodigo);
    List<InventarioItem> findByIds(List<Long> ids);
    void deleteById(Long id);
}
