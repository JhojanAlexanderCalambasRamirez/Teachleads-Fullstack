package com.techleads.application.usecase.auth;

import com.techleads.application.dto.request.LoginRequest;
import com.techleads.application.dto.response.LoginResponse;
import com.techleads.domain.model.Usuario;
import com.techleads.domain.port.UsuarioRepository;
import com.techleads.infrastructure.security.jwt.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthUseCaseTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthUseCase authUseCase;

    private Usuario usuarioAdmin;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        usuarioAdmin = Usuario.builder()
                .id(1L)
                .correo("admin@techleads.com")
                .passwordHash("$2a$12$hashedPassword")
                .nombre("Administrador")
                .rol(Usuario.Rol.ADMIN)
                .activo(true)
                .build();

        loginRequest = new LoginRequest();
        loginRequest.setCorreo("admin@techleads.com");
        loginRequest.setPassword("admin123");
    }

    @Test
    void login_debeRetornarToken_cuandoCredencialesValidas() {
        when(usuarioRepository.findByCorreo("admin@techleads.com")).thenReturn(Optional.of(usuarioAdmin));
        when(passwordEncoder.matches("admin123", "$2a$12$hashedPassword")).thenReturn(true);
        when(jwtService.generateToken(usuarioAdmin)).thenReturn("jwt-token");

        LoginResponse result = authUseCase.login(loginRequest);

        assertThat(result.getToken()).isEqualTo("jwt-token");
        assertThat(result.getRol()).isEqualTo("ADMIN");
        assertThat(result.getCorreo()).isEqualTo("admin@techleads.com");
    }

    @Test
    void login_debeLanzarExcepcion_cuandoCorreoNoExiste() {
        when(usuarioRepository.findByCorreo("noexiste@test.com")).thenReturn(Optional.empty());

        loginRequest.setCorreo("noexiste@test.com");

        assertThatThrownBy(() -> authUseCase.login(loginRequest))
                .isInstanceOf(BadCredentialsException.class);
    }

    @Test
    void login_debeLanzarExcepcion_cuandoPasswordIncorrecta() {
        when(usuarioRepository.findByCorreo("admin@techleads.com")).thenReturn(Optional.of(usuarioAdmin));
        when(passwordEncoder.matches("wrongpass", "$2a$12$hashedPassword")).thenReturn(false);

        loginRequest.setPassword("wrongpass");

        assertThatThrownBy(() -> authUseCase.login(loginRequest))
                .isInstanceOf(BadCredentialsException.class);
    }

    @Test
    void login_debeLanzarExcepcion_cuandoUsuarioInactivo() {
        Usuario inactivo = Usuario.builder()
                .id(2L)
                .correo("admin@techleads.com")
                .passwordHash("$2a$12$hashedPassword")
                .nombre("Inactivo")
                .rol(Usuario.Rol.ADMIN)
                .activo(false)
                .build();

        when(usuarioRepository.findByCorreo("admin@techleads.com")).thenReturn(Optional.of(inactivo));

        assertThatThrownBy(() -> authUseCase.login(loginRequest))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessageContaining("inactivo");
    }
}
