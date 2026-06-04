package com.techleads.application.usecase.empresa;

import com.techleads.application.dto.request.EmpresaRequest;
import com.techleads.application.dto.response.EmpresaResponse;
import com.techleads.domain.exception.EmpresaNotFoundException;
import com.techleads.domain.exception.NitDuplicadoException;
import com.techleads.domain.model.Empresa;
import com.techleads.domain.port.EmpresaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmpresaUseCase {

    private final EmpresaRepository empresaRepository;

    @Transactional
    public EmpresaResponse crear(EmpresaRequest request) {
        if (empresaRepository.existsByNit(request.getNit())) {
            throw new NitDuplicadoException(request.getNit());
        }
        return toResponse(empresaRepository.save(toModel(request)));
    }

    @Transactional
    public EmpresaResponse actualizar(String nit, EmpresaRequest request) {
        empresaRepository.findByNit(nit)
                .orElseThrow(() -> new EmpresaNotFoundException(nit));
        Empresa updated = empresaRepository.save(
                Empresa.builder()
                        .nit(nit)
                        .nombre(request.getNombre())
                        .direccion(request.getDireccion())
                        .telefono(request.getTelefono())
                        .build());
        return toResponse(updated);
    }

    @Transactional(readOnly = true)
    public EmpresaResponse obtenerPorNit(String nit) {
        return empresaRepository.findByNit(nit)
                .map(this::toResponse)
                .orElseThrow(() -> new EmpresaNotFoundException(nit));
    }

    @Transactional(readOnly = true)
    public List<EmpresaResponse> listar() {
        return empresaRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void eliminar(String nit) {
        if (!empresaRepository.existsByNit(nit)) {
            throw new EmpresaNotFoundException(nit);
        }
        empresaRepository.deleteByNit(nit);
    }

    private Empresa toModel(EmpresaRequest r) {
        return Empresa.builder()
                .nit(r.getNit())
                .nombre(r.getNombre())
                .direccion(r.getDireccion())
                .telefono(r.getTelefono())
                .build();
    }

    public EmpresaResponse toResponse(Empresa e) {
        return EmpresaResponse.builder()
                .nit(e.getNit())
                .nombre(e.getNombre())
                .direccion(e.getDireccion())
                .telefono(e.getTelefono())
                .build();
    }
}
