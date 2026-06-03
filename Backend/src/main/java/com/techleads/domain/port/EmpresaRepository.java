package com.techleads.domain.port;

import com.techleads.domain.model.Empresa;

import java.util.List;
import java.util.Optional;

public interface EmpresaRepository {
    Empresa save(Empresa empresa);
    Optional<Empresa> findByNit(String nit);
    List<Empresa> findAll();
    void deleteByNit(String nit);
    boolean existsByNit(String nit);
}
