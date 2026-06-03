package com.techleads.infrastructure.persistence.adapter;

import com.techleads.domain.model.Categoria;
import com.techleads.domain.port.CategoriaRepository;
import com.techleads.infrastructure.persistence.entity.CategoriaEntity;
import com.techleads.infrastructure.persistence.repository.JpaCategoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CategoriaRepositoryAdapter implements CategoriaRepository {

    private final JpaCategoriaRepository jpa;

    @Override
    public Categoria save(Categoria categoria) {
        CategoriaEntity entity = CategoriaEntity.builder()
                .id(categoria.getId())
                .nombre(categoria.getNombre())
                .build();
        return toDomain(jpa.save(entity));
    }

    @Override
    public Optional<Categoria> findById(Long id) {
        return jpa.findById(id).map(this::toDomain);
    }

    @Override
    public List<Categoria> findAll() {
        return jpa.findAll().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Categoria> findAllByIds(List<Long> ids) {
        return jpa.findByIdIn(ids).stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        jpa.deleteById(id);
    }

    private Categoria toDomain(CategoriaEntity e) {
        return Categoria.builder().id(e.getId()).nombre(e.getNombre()).build();
    }
}
