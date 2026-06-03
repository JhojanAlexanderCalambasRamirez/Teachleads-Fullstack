package com.techleads.infrastructure.persistence.repository;

import com.techleads.infrastructure.persistence.entity.CategoriaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaCategoriaRepository extends JpaRepository<CategoriaEntity, Long> {
    List<CategoriaEntity> findByIdIn(List<Long> ids);
}
