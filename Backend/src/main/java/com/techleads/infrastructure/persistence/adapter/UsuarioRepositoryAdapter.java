package com.techleads.infrastructure.persistence.adapter;

import com.techleads.domain.model.Usuario;
import com.techleads.domain.port.UsuarioRepository;
import com.techleads.infrastructure.persistence.entity.UsuarioEntity;
import com.techleads.infrastructure.persistence.repository.JpaUsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UsuarioRepositoryAdapter implements UsuarioRepository {

    private final JpaUsuarioRepository jpa;

    @Override
    public Optional<Usuario> findByCorreo(String correo) {
        return jpa.findByCorreo(correo).map(this::toDomain);
    }

    @Override
    public boolean existsByCorreo(String correo) {
        return jpa.existsByCorreo(correo);
    }

    @Override
    public Usuario save(Usuario usuario) {
        UsuarioEntity entity = UsuarioEntity.builder()
                .id(usuario.getId())
                .correo(usuario.getCorreo())
                .passwordHash(usuario.getPasswordHash())
                .nombre(usuario.getNombre())
                .rol(usuario.getRol().name())
                .activo(usuario.isActivo())
                .build();
        return toDomain(jpa.save(entity));
    }

    private Usuario toDomain(UsuarioEntity e) {
        return Usuario.builder()
                .id(e.getId())
                .correo(e.getCorreo())
                .passwordHash(e.getPasswordHash())
                .nombre(e.getNombre())
                .rol(Usuario.Rol.valueOf(e.getRol()))
                .activo(e.isActivo())
                .build();
    }
}
