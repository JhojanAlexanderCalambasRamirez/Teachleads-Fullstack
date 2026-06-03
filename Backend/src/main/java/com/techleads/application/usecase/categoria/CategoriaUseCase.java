package com.techleads.application.usecase.categoria;

import com.techleads.application.dto.response.CategoriaResponse;
import com.techleads.domain.model.Categoria;
import com.techleads.domain.port.CategoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoriaUseCase {

    private final CategoriaRepository categoriaRepository;

    public List<CategoriaResponse> listar() {
        return categoriaRepository.findAll().stream()
                .map(c -> CategoriaResponse.builder()
                        .id(c.getId())
                        .nombre(c.getNombre())
                        .build())
                .collect(Collectors.toList());
    }

    public CategoriaResponse crear(String nombre) {
        Categoria saved = categoriaRepository.save(
                Categoria.builder().nombre(nombre).build());
        return CategoriaResponse.builder()
                .id(saved.getId())
                .nombre(saved.getNombre())
                .build();
    }
}
