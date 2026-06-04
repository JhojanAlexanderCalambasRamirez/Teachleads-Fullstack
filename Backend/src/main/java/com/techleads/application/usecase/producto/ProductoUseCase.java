package com.techleads.application.usecase.producto;

import com.techleads.application.dto.request.ProductoRequest;
import com.techleads.application.dto.response.CategoriaResponse;
import com.techleads.application.dto.response.ProductoPrecioResponse;
import com.techleads.application.dto.response.ProductoResponse;
import com.techleads.domain.exception.EmpresaNotFoundException;
import com.techleads.domain.exception.ProductoCodigoDuplicadoException;
import com.techleads.domain.exception.ProductoNotFoundException;
import com.techleads.domain.model.Categoria;
import com.techleads.domain.model.Producto;
import com.techleads.domain.model.ProductoPrecio;
import com.techleads.domain.port.CategoriaRepository;
import com.techleads.domain.port.EmpresaRepository;
import com.techleads.domain.port.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductoUseCase {

    private final ProductoRepository productoRepository;
    private final EmpresaRepository empresaRepository;
    private final CategoriaRepository categoriaRepository;

    @Transactional
    public ProductoResponse crear(ProductoRequest request) {
        if (productoRepository.existsByCodigo(request.getCodigo())) {
            throw new ProductoCodigoDuplicadoException(request.getCodigo());
        }
        empresaRepository.findByNit(request.getEmpresaNit())
                .orElseThrow(() -> new EmpresaNotFoundException(request.getEmpresaNit()));

        return toResponse(productoRepository.save(toModel(request)), request.getEmpresaNit());
    }

    @Transactional
    public ProductoResponse actualizar(String codigo, ProductoRequest request) {
        productoRepository.findByCodigo(codigo)
                .orElseThrow(() -> new ProductoNotFoundException(codigo));
        empresaRepository.findByNit(request.getEmpresaNit())
                .orElseThrow(() -> new EmpresaNotFoundException(request.getEmpresaNit()));

        return toResponse(
                productoRepository.save(toModel(request).toBuilder().codigo(codigo).build()),
                request.getEmpresaNit());
    }

    @Transactional(readOnly = true)
    public ProductoResponse obtenerPorCodigo(String codigo) {
        Producto p = productoRepository.findByCodigo(codigo)
                .orElseThrow(() -> new ProductoNotFoundException(codigo));
        return toResponse(p, p.getEmpresaNit());
    }

    @Transactional(readOnly = true)
    public List<ProductoResponse> listar() {
        return productoRepository.findAll().stream()
                .map(p -> toResponse(p, p.getEmpresaNit()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductoResponse> listarPorEmpresa(String empresaNit) {
        return productoRepository.findByEmpresaNit(empresaNit).stream()
                .map(p -> toResponse(p, empresaNit))
                .collect(Collectors.toList());
    }

    @Transactional
    public void eliminar(String codigo) {
        if (!productoRepository.existsByCodigo(codigo)) {
            throw new ProductoNotFoundException(codigo);
        }
        productoRepository.deleteByCodigo(codigo);
    }

    private Producto toModel(ProductoRequest r) {
        List<ProductoPrecio> precios = r.getPrecios() == null ? Collections.emptyList() :
                r.getPrecios().stream()
                        .map(p -> ProductoPrecio.builder()
                                .moneda(p.getMoneda())
                                .precio(p.getPrecio())
                                .build())
                        .collect(Collectors.toList());

        List<Categoria> categorias = r.getCategoriaIds() == null ? Collections.emptyList() :
                categoriaRepository.findAllByIds(r.getCategoriaIds());

        return Producto.builder()
                .codigo(r.getCodigo())
                .nombre(r.getNombre())
                .caracteristicas(r.getCaracteristicas())
                .empresaNit(r.getEmpresaNit())
                .precios(precios)
                .categorias(categorias)
                .build();
    }

    public ProductoResponse toResponse(Producto p, String empresaNit) {
        List<ProductoPrecioResponse> precios = p.getPrecios() == null ? Collections.emptyList() :
                p.getPrecios().stream()
                        .map(pp -> ProductoPrecioResponse.builder()
                                .id(pp.getId())
                                .moneda(pp.getMoneda())
                                .precio(pp.getPrecio())
                                .build())
                        .collect(Collectors.toList());

        List<CategoriaResponse> categorias = p.getCategorias() == null ? Collections.emptyList() :
                p.getCategorias().stream()
                        .map(c -> CategoriaResponse.builder()
                                .id(c.getId())
                                .nombre(c.getNombre())
                                .build())
                        .collect(Collectors.toList());

        String empresaNombre = empresaRepository.findByNit(empresaNit)
                .map(e -> e.getNombre())
                .orElse(null);

        return ProductoResponse.builder()
                .codigo(p.getCodigo())
                .nombre(p.getNombre())
                .caracteristicas(p.getCaracteristicas())
                .empresaNit(p.getEmpresaNit())
                .empresaNombre(empresaNombre)
                .precios(precios)
                .categorias(categorias)
                .build();
    }
}
