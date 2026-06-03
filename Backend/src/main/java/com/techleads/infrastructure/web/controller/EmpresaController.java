package com.techleads.infrastructure.web.controller;

import com.techleads.application.dto.request.EmpresaRequest;
import com.techleads.application.dto.response.ApiResponse;
import com.techleads.application.dto.response.EmpresaResponse;
import com.techleads.application.usecase.empresa.EmpresaUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/empresas")
@RequiredArgsConstructor
public class EmpresaController {

    private final EmpresaUseCase empresaUseCase;

    @PostMapping
    public ResponseEntity<ApiResponse<EmpresaResponse>> crear(@Valid @RequestBody EmpresaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Empresa creada", empresaUseCase.crear(request)));
    }

    @PutMapping("/{nit}")
    public ResponseEntity<ApiResponse<EmpresaResponse>> actualizar(
            @PathVariable String nit,
            @Valid @RequestBody EmpresaRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Empresa actualizada", empresaUseCase.actualizar(nit, request)));
    }

    @GetMapping("/{nit}")
    public ResponseEntity<ApiResponse<EmpresaResponse>> obtener(@PathVariable String nit) {
        return ResponseEntity.ok(ApiResponse.ok(empresaUseCase.obtenerPorNit(nit)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<EmpresaResponse>>> listar() {
        return ResponseEntity.ok(ApiResponse.ok(empresaUseCase.listar()));
    }

    @DeleteMapping("/{nit}")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable String nit) {
        empresaUseCase.eliminar(nit);
        return ResponseEntity.ok(ApiResponse.ok("Empresa eliminada", null));
    }
}
