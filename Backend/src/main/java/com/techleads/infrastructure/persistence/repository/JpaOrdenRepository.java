package com.techleads.infrastructure.persistence.repository;

import com.techleads.infrastructure.persistence.entity.OrdenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaOrdenRepository extends JpaRepository<OrdenEntity, Long> {
    List<OrdenEntity> findByCliente_Id(Long clienteId);
}
