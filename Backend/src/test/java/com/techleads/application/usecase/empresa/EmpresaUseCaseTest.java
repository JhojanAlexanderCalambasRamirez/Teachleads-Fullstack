package com.techleads.application.usecase.empresa;

import com.techleads.application.dto.request.EmpresaRequest;
import com.techleads.application.dto.response.EmpresaResponse;
import com.techleads.domain.model.Empresa;
import com.techleads.domain.port.EmpresaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmpresaUseCaseTest {

    @Mock
    private EmpresaRepository empresaRepository;

    @InjectMocks
    private EmpresaUseCase empresaUseCase;

    private EmpresaRequest request;
    private Empresa empresa;

    @BeforeEach
    void setUp() {
        request = new EmpresaRequest();
        request.setNit("123456789");
        request.setNombre("TechLeads S.A.S");
        request.setDireccion("Calle 123 # 45-67");
        request.setTelefono("3001234567");

        empresa = Empresa.builder()
                .nit("123456789")
                .nombre("TechLeads S.A.S")
                .direccion("Calle 123 # 45-67")
                .telefono("3001234567")
                .build();
    }

    @Test
    void crear_debeRetornarEmpresa_cuandoNitNoExiste() {
        when(empresaRepository.existsByNit("123456789")).thenReturn(false);
        when(empresaRepository.save(any())).thenReturn(empresa);

        EmpresaResponse result = empresaUseCase.crear(request);

        assertThat(result.getNit()).isEqualTo("123456789");
        assertThat(result.getNombre()).isEqualTo("TechLeads S.A.S");
        verify(empresaRepository).save(any());
    }

    @Test
    void crear_debeLanzarExcepcion_cuandoNitYaExiste() {
        when(empresaRepository.existsByNit("123456789")).thenReturn(true);

        assertThatThrownBy(() -> empresaUseCase.crear(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("123456789");

        verify(empresaRepository, never()).save(any());
    }

    @Test
    void listar_debeRetornarTodasLasEmpresas() {
        when(empresaRepository.findAll()).thenReturn(List.of(empresa));

        List<EmpresaResponse> result = empresaUseCase.listar();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getNit()).isEqualTo("123456789");
    }

    @Test
    void obtenerPorNit_debeLanzarExcepcion_cuandoNoExiste() {
        when(empresaRepository.findByNit("999")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> empresaUseCase.obtenerPorNit("999"))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void eliminar_debeLanzarExcepcion_cuandoNoExiste() {
        when(empresaRepository.existsByNit("999")).thenReturn(false);

        assertThatThrownBy(() -> empresaUseCase.eliminar("999"))
                .isInstanceOf(NoSuchElementException.class);

        verify(empresaRepository, never()).deleteByNit(any());
    }
}
