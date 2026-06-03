package com.techleads.infrastructure.persistence.adapter;

import com.techleads.domain.model.Empresa;
import com.techleads.domain.port.EmpresaRepository;
import com.techleads.infrastructure.persistence.entity.EmpresaEntity;
import com.techleads.infrastructure.persistence.repository.JpaEmpresaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class EmpresaRepositoryAdapter implements EmpresaRepository {

    private final JpaEmpresaRepository jpa;

    @Override
    public Empresa save(Empresa empresa) {
        EmpresaEntity saved = jpa.save(toEntity(empresa));
        return toDomain(saved);
    }

    @Override
    public Optional<Empresa> findByNit(String nit) {
        return jpa.findById(nit).map(this::toDomain);
    }

    @Override
    public List<Empresa> findAll() {
        return jpa.findAll().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public void deleteByNit(String nit) {
        jpa.deleteById(nit);
    }

    @Override
    public boolean existsByNit(String nit) {
        return jpa.existsById(nit);
    }

    private EmpresaEntity toEntity(Empresa e) {
        return EmpresaEntity.builder()
                .nit(e.getNit())
                .nombre(e.getNombre())
                .direccion(e.getDireccion())
                .telefono(e.getTelefono())
                .build();
    }

    private Empresa toDomain(EmpresaEntity e) {
        return Empresa.builder()
                .nit(e.getNit())
                .nombre(e.getNombre())
                .direccion(e.getDireccion())
                .telefono(e.getTelefono())
                .build();
    }
}
