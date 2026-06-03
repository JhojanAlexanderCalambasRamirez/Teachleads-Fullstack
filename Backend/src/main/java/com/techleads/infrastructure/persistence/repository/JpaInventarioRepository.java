package com.techleads.infrastructure.persistence.repository;

import com.techleads.infrastructure.persistence.entity.InventarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface JpaInventarioRepository extends JpaRepository<InventarioEntity, Long> {
    List<InventarioEntity> findByEmpresa_Nit(String empresaNit);

    @Query("SELECT i FROM InventarioEntity i WHERE i.empresa.nit = :empresaNit AND i.producto.codigo = :productoCodigo")
    Optional<InventarioEntity> findByEmpresaNitAndProductoCodigo(String empresaNit, String productoCodigo);
}
