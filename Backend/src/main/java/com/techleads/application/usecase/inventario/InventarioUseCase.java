package com.techleads.application.usecase.inventario;

import com.techleads.application.dto.request.InventarioRequest;
import com.techleads.application.dto.response.InventarioResponse;
import com.techleads.domain.model.InventarioItem;
import com.techleads.domain.port.EmpresaRepository;
import com.techleads.domain.port.InventarioRepository;
import com.techleads.domain.port.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventarioUseCase {

    private final InventarioRepository inventarioRepository;
    private final EmpresaRepository empresaRepository;
    private final ProductoRepository productoRepository;

    public InventarioResponse agregar(InventarioRequest request) {
        empresaRepository.findByNit(request.getEmpresaNit())
                .orElseThrow(() -> new NoSuchElementException("Empresa no encontrada: " + request.getEmpresaNit()));
        productoRepository.findByCodigo(request.getProductoCodigo())
                .orElseThrow(() -> new NoSuchElementException("Producto no encontrado: " + request.getProductoCodigo()));

        InventarioItem item = inventarioRepository
                .findByEmpresaNitAndProductoCodigo(request.getEmpresaNit(), request.getProductoCodigo())
                .map(existing -> InventarioItem.builder()
                        .id(existing.getId())
                        .empresaNit(existing.getEmpresaNit())
                        .empresaNombre(existing.getEmpresaNombre())
                        .productoCodigo(existing.getProductoCodigo())
                        .productoNombre(existing.getProductoNombre())
                        .productoCaracteristicas(existing.getProductoCaracteristicas())
                        .cantidad(existing.getCantidad() + request.getCantidad())
                        .build())
                .orElse(InventarioItem.builder()
                        .empresaNit(request.getEmpresaNit())
                        .productoCodigo(request.getProductoCodigo())
                        .cantidad(request.getCantidad())
                        .build());

        return toResponse(inventarioRepository.save(item));
    }

    public List<InventarioResponse> listar() {
        return inventarioRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<InventarioResponse> listarPorEmpresa(String empresaNit) {
        return inventarioRepository.findByEmpresaNit(empresaNit).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<InventarioResponse> listarPorIds(List<Long> ids) {
        return inventarioRepository.findByIds(ids).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public void eliminar(Long id) {
        inventarioRepository.deleteById(id);
    }

    private InventarioResponse toResponse(InventarioItem i) {
        return InventarioResponse.builder()
                .id(i.getId())
                .empresaNit(i.getEmpresaNit())
                .empresaNombre(i.getEmpresaNombre())
                .productoCodigo(i.getProductoCodigo())
                .productoNombre(i.getProductoNombre())
                .productoCaracteristicas(i.getProductoCaracteristicas())
                .cantidad(i.getCantidad())
                .build();
    }
}
