package com.techleads.domain.port;

import com.techleads.domain.model.Usuario;

import java.util.Optional;

public interface UsuarioRepository {
    Optional<Usuario> findByCorreo(String correo);
    boolean existsByCorreo(String correo);
    Usuario save(Usuario usuario);
}
