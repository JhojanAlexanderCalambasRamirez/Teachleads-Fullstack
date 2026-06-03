package com.techleads.infrastructure.persistence.adapter;

import com.techleads.domain.model.Cliente;
import com.techleads.domain.port.ClienteRepository;
import com.techleads.infrastructure.persistence.entity.ClienteEntity;
import com.techleads.infrastructure.persistence.repository.JpaClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ClienteRepositoryAdapter implements ClienteRepository {

    private final JpaClienteRepository jpa;

    @Override
    public Cliente save(Cliente cliente) {
        ClienteEntity entity = ClienteEntity.builder()
                .id(cliente.getId())
                .nombre(cliente.getNombre())
                .correo(cliente.getCorreo())
                .build();
        return toDomain(jpa.save(entity));
    }

    @Override
    public Optional<Cliente> findById(Long id) {
        return jpa.findById(id).map(this::toDomain);
    }

    @Override
    public List<Cliente> findAll() {
        return jpa.findAll().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        jpa.deleteById(id);
    }

    private Cliente toDomain(ClienteEntity e) {
        return Cliente.builder().id(e.getId()).nombre(e.getNombre()).correo(e.getCorreo()).build();
    }
}
