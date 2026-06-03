package com.techleads.infrastructure.web.controller;

import com.techleads.application.dto.request.InventarioRequest;
import com.techleads.application.dto.request.SendPdfRequest;
import com.techleads.application.dto.response.ApiResponse;
import com.techleads.application.dto.response.InventarioResponse;
import com.techleads.application.usecase.inventario.InventarioUseCase;
import com.techleads.infrastructure.email.EmailService;
import com.techleads.infrastructure.pdf.PdfService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventario")
@RequiredArgsConstructor
public class InventarioController {

    private final InventarioUseCase inventarioUseCase;
    private final PdfService pdfService;
    private final EmailService emailService;

    @PostMapping
    public ResponseEntity<ApiResponse<InventarioResponse>> agregar(@Valid @RequestBody InventarioRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Inventario actualizado", inventarioUseCase.agregar(request)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<InventarioResponse>>> listar(
            @RequestParam(required = false) String empresaNit) {
        List<InventarioResponse> result = empresaNit != null
                ? inventarioUseCase.listarPorEmpresa(empresaNit)
                : inventarioUseCase.listar();
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        inventarioUseCase.eliminar(id);
        return ResponseEntity.ok(ApiResponse.ok("Item eliminado", null));
    }

    @GetMapping("/pdf")
    public ResponseEntity<byte[]> descargarPdf(
            @RequestParam(required = false) String empresaNit,
            @RequestParam(required = false) List<Long> ids) {

        List<InventarioResponse> inventario = resolverInventario(ids, empresaNit);
        byte[] pdf = pdfService.generarInventarioPdf(inventario);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=inventario.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @PostMapping("/enviar-pdf")
    public ResponseEntity<ApiResponse<Void>> enviarPdf(@Valid @RequestBody SendPdfRequest request) {
        List<InventarioResponse> inventario = resolverInventario(request.getIds(), request.getEmpresaNit());
        byte[] pdf = pdfService.generarInventarioPdf(inventario);
        emailService.enviarInventarioPdf(request.getDestinatario(), pdf);
        return ResponseEntity.ok(ApiResponse.ok("PDF enviado a " + request.getDestinatario(), null));
    }

    private List<InventarioResponse> resolverInventario(List<Long> ids, String empresaNit) {
        if (ids != null && !ids.isEmpty()) {
            return inventarioUseCase.listarPorIds(ids);
        }
        if (empresaNit != null && !empresaNit.isBlank()) {
            return inventarioUseCase.listarPorEmpresa(empresaNit);
        }
        return inventarioUseCase.listar();
    }
}
