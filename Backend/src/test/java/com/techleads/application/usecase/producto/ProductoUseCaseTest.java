package com.techleads.application.usecase.producto;

import com.techleads.application.dto.request.ProductoPrecioRequest;
import com.techleads.application.dto.request.ProductoRequest;
import com.techleads.application.dto.response.ProductoResponse;
import com.techleads.domain.exception.EmpresaNotFoundException;
import com.techleads.domain.exception.ProductoCodigoDuplicadoException;
import com.techleads.domain.exception.ProductoNotFoundException;
import com.techleads.domain.model.Empresa;
import com.techleads.domain.model.Producto;
import com.techleads.domain.port.CategoriaRepository;
import com.techleads.domain.port.EmpresaRepository;
import com.techleads.domain.port.ProductoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductoUseCaseTest {

    @Mock
    private ProductoRepository productoRepository;
    @Mock
    private EmpresaRepository empresaRepository;
    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private ProductoUseCase productoUseCase;

    private ProductoRequest request;
    private Producto producto;
    private Empresa empresa;

    @BeforeEach
    void setUp() {
        empresa = Empresa.builder()
                .nit("123456789")
                .nombre("TechLeads SAS")
                .direccion("Calle 1")
                .telefono("3001234567")
                .build();

        ProductoPrecioRequest precio = new ProductoPrecioRequest();
        precio.setMoneda("COP");
        precio.setPrecio(new BigDecimal("4500000"));

        request = new ProductoRequest();
        request.setCodigo("LAPTOP-001");
        request.setNombre("Laptop Dell XPS");
        request.setCaracteristicas("Intel i7, 16GB RAM");
        request.setEmpresaNit("123456789");
        request.setPrecios(List.of(precio));
        request.setCategoriaIds(Collections.emptyList());

        producto = Producto.builder()
                .codigo("LAPTOP-001")
                .nombre("Laptop Dell XPS")
                .caracteristicas("Intel i7, 16GB RAM")
                .empresaNit("123456789")
                .precios(Collections.emptyList())
                .categorias(Collections.emptyList())
                .build();
    }

    @Test
    void crear_debeRetornarProducto_cuandoCodigoNoExisteYEmpresaExiste() {
        when(productoRepository.existsByCodigo("LAPTOP-001")).thenReturn(false);
        when(empresaRepository.findByNit("123456789")).thenReturn(Optional.of(empresa));
        when(categoriaRepository.findAllByIds(any())).thenReturn(Collections.emptyList());
        when(productoRepository.save(any())).thenReturn(producto);

        ProductoResponse result = productoUseCase.crear(request);

        assertThat(result.getCodigo()).isEqualTo("LAPTOP-001");
        assertThat(result.getNombre()).isEqualTo("Laptop Dell XPS");
        verify(productoRepository).save(any());
    }

    @Test
    void crear_debeLanzarExcepcion_cuandoCodigoYaExiste() {
        when(productoRepository.existsByCodigo("LAPTOP-001")).thenReturn(true);

        assertThatThrownBy(() -> productoUseCase.crear(request))
                .isInstanceOf(ProductoCodigoDuplicadoException.class)
                .hasMessageContaining("LAPTOP-001");

        verify(productoRepository, never()).save(any());
    }

    @Test
    void crear_debeLanzarExcepcion_cuandoEmpresaNoExiste() {
        when(productoRepository.existsByCodigo("LAPTOP-001")).thenReturn(false);
        when(empresaRepository.findByNit("123456789")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productoUseCase.crear(request))
                .isInstanceOf(EmpresaNotFoundException.class)
                .hasMessageContaining("123456789");

        verify(productoRepository, never()).save(any());
    }

    @Test
    void obtenerPorCodigo_debeLanzarExcepcion_cuandoNoExiste() {
        when(productoRepository.findByCodigo("NOEXISTE")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productoUseCase.obtenerPorCodigo("NOEXISTE"))
                .isInstanceOf(ProductoNotFoundException.class);
    }

    @Test
    void eliminar_debeLanzarExcepcion_cuandoNoExiste() {
        when(productoRepository.existsByCodigo("NOEXISTE")).thenReturn(false);

        assertThatThrownBy(() -> productoUseCase.eliminar("NOEXISTE"))
                .isInstanceOf(ProductoNotFoundException.class);

        verify(productoRepository, never()).deleteByCodigo(any());
    }

    @Test
    void listarPorEmpresa_debeRetornarProductosFiltrados() {
        when(productoRepository.findByEmpresaNit("123456789")).thenReturn(List.of(producto));
        when(empresaRepository.findByNit("123456789")).thenReturn(Optional.of(empresa));

        List<ProductoResponse> result = productoUseCase.listarPorEmpresa("123456789");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEmpresaNit()).isEqualTo("123456789");
    }
}
