package com.techleads.infrastructure.persistence.adapter;

import com.techleads.domain.model.Categoria;
import com.techleads.domain.model.Producto;
import com.techleads.domain.model.ProductoPrecio;
import com.techleads.domain.port.ProductoRepository;
import com.techleads.infrastructure.persistence.entity.*;
import com.techleads.infrastructure.persistence.repository.JpaCategoriaRepository;
import com.techleads.infrastructure.persistence.repository.JpaEmpresaRepository;
import com.techleads.infrastructure.persistence.repository.JpaProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProductoRepositoryAdapter implements ProductoRepository {

    private final JpaProductoRepository jpa;
    private final JpaEmpresaRepository jpaEmpresa;
    private final JpaCategoriaRepository jpaCategoria;

    @Override
    public Producto save(Producto producto) {
        EmpresaEntity empresa = jpaEmpresa.findById(producto.getEmpresaNit())
                .orElseThrow(() -> new RuntimeException("Empresa not found"));

        ProductoEntity entity = jpa.findById(producto.getCodigo()).orElse(
                ProductoEntity.builder().codigo(producto.getCodigo()).build());

        entity.setCodigo(producto.getCodigo());
        entity.setNombre(producto.getNombre());
        entity.setCaracteristicas(producto.getCaracteristicas());
        entity.setEmpresa(empresa);

        entity.getPrecios().clear();
        if (producto.getPrecios() != null) {
            producto.getPrecios().forEach(pp -> {
                ProductoPrecioEntity precioEntity = ProductoPrecioEntity.builder()
                        .id(pp.getId())
                        .producto(entity)
                        .moneda(pp.getMoneda())
                        .precio(pp.getPrecio())
                        .build();
                entity.getPrecios().add(precioEntity);
            });
        }

        if (producto.getCategorias() != null) {
            List<Long> categoriaIds = producto.getCategorias().stream()
                    .map(Categoria::getId).collect(Collectors.toList());
            entity.setCategorias(new HashSet<>(jpaCategoria.findByIdIn(categoriaIds)));
        }

        return toDomain(jpa.save(entity));
    }

    @Override
    public Optional<Producto> findByCodigo(String codigo) {
        return jpa.findById(codigo).map(this::toDomain);
    }

    @Override
    public List<Producto> findAll() {
        return jpa.findAll().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Producto> findByEmpresaNit(String empresaNit) {
        return jpa.findByEmpresa_Nit(empresaNit).stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public void deleteByCodigo(String codigo) {
        jpa.deleteById(codigo);
    }

    @Override
    public boolean existsByCodigo(String codigo) {
        return jpa.existsById(codigo);
    }

    private Producto toDomain(ProductoEntity e) {
        List<ProductoPrecio> precios = e.getPrecios().stream()
                .map(pp -> ProductoPrecio.builder()
                        .id(pp.getId())
                        .moneda(pp.getMoneda())
                        .precio(pp.getPrecio())
                        .build())
                .collect(Collectors.toList());

        List<Categoria> categorias = e.getCategorias().stream()
                .map(c -> Categoria.builder().id(c.getId()).nombre(c.getNombre()).build())
                .collect(Collectors.toList());

        return Producto.builder()
                .codigo(e.getCodigo())
                .nombre(e.getNombre())
                .caracteristicas(e.getCaracteristicas())
                .empresaNit(e.getEmpresa().getNit())
                .precios(precios)
                .categorias(categorias)
                .build();
    }
}
