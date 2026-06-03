package com.techleads.domain.port;

import com.techleads.domain.model.Categoria;

import java.util.List;
import java.util.Optional;

public interface CategoriaRepository {
    Categoria save(Categoria categoria);
    Optional<Categoria> findById(Long id);
    List<Categoria> findAll();
    List<Categoria> findAllByIds(List<Long> ids);
    void deleteById(Long id);
}
