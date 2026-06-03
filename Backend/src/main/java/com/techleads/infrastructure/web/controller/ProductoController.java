package com.techleads.infrastructure.web.controller;

import com.techleads.application.dto.request.ProductoRequest;
import com.techleads.application.dto.response.ApiResponse;
import com.techleads.application.dto.response.ProductoResponse;
import com.techleads.application.usecase.producto.ProductoUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoUseCase productoUseCase;

    @PostMapping
    public ResponseEntity<ApiResponse<ProductoResponse>> crear(@Valid @RequestBody ProductoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Producto creado", productoUseCase.crear(request)));
    }

    @PutMapping("/{codigo}")
    public ResponseEntity<ApiResponse<ProductoResponse>> actualizar(
            @PathVariable String codigo,
            @Valid @RequestBody ProductoRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Producto actualizado", productoUseCase.actualizar(codigo, request)));
    }

    @GetMapping("/{codigo}")
    public ResponseEntity<ApiResponse<ProductoResponse>> obtener(@PathVariable String codigo) {
        return ResponseEntity.ok(ApiResponse.ok(productoUseCase.obtenerPorCodigo(codigo)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductoResponse>>> listar(
            @RequestParam(required = false) String empresaNit) {
        List<ProductoResponse> result = empresaNit != null
                ? productoUseCase.listarPorEmpresa(empresaNit)
                : productoUseCase.listar();
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    @DeleteMapping("/{codigo}")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable String codigo) {
        productoUseCase.eliminar(codigo);
        return ResponseEntity.ok(ApiResponse.ok("Producto eliminado", null));
    }
}
