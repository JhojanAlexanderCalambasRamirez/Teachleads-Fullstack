package com.techleads.infrastructure.persistence.repository;

import com.techleads.infrastructure.persistence.entity.ProductoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaProductoRepository extends JpaRepository<ProductoEntity, String> {
    List<ProductoEntity> findByEmpresa_Nit(String empresaNit);
}
