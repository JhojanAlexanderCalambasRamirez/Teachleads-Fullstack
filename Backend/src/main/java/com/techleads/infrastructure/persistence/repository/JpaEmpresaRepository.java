package com.techleads.infrastructure.persistence.repository;

import com.techleads.infrastructure.persistence.entity.EmpresaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaEmpresaRepository extends JpaRepository<EmpresaEntity, String> {
}
