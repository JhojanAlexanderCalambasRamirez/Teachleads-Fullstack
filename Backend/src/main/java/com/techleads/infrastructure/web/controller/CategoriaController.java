package com.techleads.infrastructure.web.controller;

import com.techleads.application.dto.response.ApiResponse;
import com.techleads.application.dto.response.CategoriaResponse;
import com.techleads.application.usecase.categoria.CategoriaUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/categorias")
@RequiredArgsConstructor
public class CategoriaController {

    private final CategoriaUseCase categoriaUseCase;

    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoriaResponse>>> listar() {
        return ResponseEntity.ok(ApiResponse.ok(categoriaUseCase.listar()));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CategoriaResponse>> crear(@RequestBody Map<String, String> body) {
        return ResponseEntity.ok(ApiResponse.ok("Categoría creada", categoriaUseCase.crear(body.get("nombre"))));
    }
}
