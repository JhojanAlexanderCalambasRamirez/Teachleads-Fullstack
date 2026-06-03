package com.techleads.infrastructure.persistence.adapter;

import com.techleads.domain.model.InventarioItem;
import com.techleads.domain.port.InventarioRepository;
import com.techleads.infrastructure.persistence.entity.EmpresaEntity;
import com.techleads.infrastructure.persistence.entity.InventarioEntity;
import com.techleads.infrastructure.persistence.entity.ProductoEntity;
import com.techleads.infrastructure.persistence.repository.JpaEmpresaRepository;
import com.techleads.infrastructure.persistence.repository.JpaInventarioRepository;
import com.techleads.infrastructure.persistence.repository.JpaProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class InventarioRepositoryAdapter implements InventarioRepository {

    private final JpaInventarioRepository jpa;
    private final JpaEmpresaRepository jpaEmpresa;
    private final JpaProductoRepository jpaProducto;

    @Override
    public InventarioItem save(InventarioItem item) {
        EmpresaEntity empresa = jpaEmpresa.findById(item.getEmpresaNit())
                .orElseThrow(() -> new RuntimeException("Empresa not found"));
        ProductoEntity producto = jpaProducto.findById(item.getProductoCodigo())
                .orElseThrow(() -> new RuntimeException("Producto not found"));

        InventarioEntity entity = InventarioEntity.builder()
                .id(item.getId())
                .empresa(empresa)
                .producto(producto)
                .cantidad(item.getCantidad())
                .build();

        return toDomain(jpa.save(entity));
    }

    @Override
    public List<InventarioItem> findAll() {
        return jpa.findAll().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<InventarioItem> findByEmpresaNit(String empresaNit) {
        return jpa.findByEmpresa_Nit(empresaNit).stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public Optional<InventarioItem> findByEmpresaNitAndProductoCodigo(String empresaNit, String productoCodigo) {
        return jpa.findByEmpresaNitAndProductoCodigo(empresaNit, productoCodigo).map(this::toDomain);
    }

    @Override
    public List<InventarioItem> findByIds(List<Long> ids) {
        return jpa.findAllById(ids).stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        jpa.deleteById(id);
    }

    private InventarioItem toDomain(InventarioEntity e) {
        return InventarioItem.builder()
                .id(e.getId())
                .empresaNit(e.getEmpresa().getNit())
                .empresaNombre(e.getEmpresa().getNombre())
                .productoCodigo(e.getProducto().getCodigo())
                .productoNombre(e.getProducto().getNombre())
                .productoCaracteristicas(e.getProducto().getCaracteristicas())
                .cantidad(e.getCantidad())
                .build();
    }
}
