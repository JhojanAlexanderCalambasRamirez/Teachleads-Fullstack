package com.techleads.application.usecase.inventario;

import com.techleads.application.dto.request.InventarioRequest;
import com.techleads.application.dto.response.InventarioResponse;
import com.techleads.domain.model.Empresa;
import com.techleads.domain.model.InventarioItem;
import com.techleads.domain.model.Producto;
import com.techleads.domain.port.EmpresaRepository;
import com.techleads.domain.port.InventarioRepository;
import com.techleads.domain.port.ProductoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventarioUseCaseTest {

    @Mock
    private InventarioRepository inventarioRepository;
    @Mock
    private EmpresaRepository empresaRepository;
    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private InventarioUseCase inventarioUseCase;

    private InventarioRequest request;

    @BeforeEach
    void setUp() {
        request = new InventarioRequest();
        request.setEmpresaNit("123456789");
        request.setProductoCodigo("PROD-001");
        request.setCantidad(10);
    }

    @Test
    void agregar_debeCrearNuevoItem_cuandoNoExisteEnInventario() {
        when(empresaRepository.findByNit("123456789")).thenReturn(
                Optional.of(Empresa.builder().nit("123456789").nombre("TechLeads").direccion("Calle 1").telefono("3001234567").build()));
        when(productoRepository.findByCodigo("PROD-001")).thenReturn(
                Optional.of(Producto.builder().codigo("PROD-001").nombre("Laptop").empresaNit("123456789").build()));
        when(inventarioRepository.findByEmpresaNitAndProductoCodigo("123456789", "PROD-001"))
                .thenReturn(Optional.empty());

        InventarioItem saved = InventarioItem.builder()
                .id(1L).empresaNit("123456789").empresaNombre("TechLeads")
                .productoCodigo("PROD-001").productoNombre("Laptop").cantidad(10).build();
        when(inventarioRepository.save(any())).thenReturn(saved);

        InventarioResponse result = inventarioUseCase.agregar(request);

        assertThat(result.getCantidad()).isEqualTo(10);
        assertThat(result.getProductoCodigo()).isEqualTo("PROD-001");
    }

    @Test
    void agregar_debeAcumularCantidad_cuandoYaExisteEnInventario() {
        when(empresaRepository.findByNit("123456789")).thenReturn(
                Optional.of(Empresa.builder().nit("123456789").nombre("TechLeads").direccion("Calle 1").telefono("3001234567").build()));
        when(productoRepository.findByCodigo("PROD-001")).thenReturn(
                Optional.of(Producto.builder().codigo("PROD-001").nombre("Laptop").empresaNit("123456789").build()));

        InventarioItem existing = InventarioItem.builder()
                .id(1L).empresaNit("123456789").empresaNombre("TechLeads")
                .productoCodigo("PROD-001").productoNombre("Laptop").cantidad(5).build();
        when(inventarioRepository.findByEmpresaNitAndProductoCodigo("123456789", "PROD-001"))
                .thenReturn(Optional.of(existing));

        InventarioItem saved = InventarioItem.builder()
                .id(1L).empresaNit("123456789").empresaNombre("TechLeads")
                .productoCodigo("PROD-001").productoNombre("Laptop").cantidad(15).build();
        when(inventarioRepository.save(any())).thenReturn(saved);

        InventarioResponse result = inventarioUseCase.agregar(request);

        assertThat(result.getCantidad()).isEqualTo(15);
    }

    @Test
    void listar_debeRetornarTodosLosItems() {
        when(inventarioRepository.findAll()).thenReturn(Collections.emptyList());

        List<InventarioResponse> result = inventarioUseCase.listar();

        assertThat(result).isEmpty();
    }
}
