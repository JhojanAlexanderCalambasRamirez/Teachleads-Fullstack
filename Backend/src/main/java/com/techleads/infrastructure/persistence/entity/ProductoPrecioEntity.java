package com.techleads.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "producto_precio",
        uniqueConstraints = @UniqueConstraint(columnNames = {"producto_codigo", "moneda"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductoPrecioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_codigo", nullable = false)
    private ProductoEntity producto;

    @Column(name = "moneda", nullable = false, length = 3)
    private String moneda;

    @Column(name = "precio", nullable = false, precision = 15, scale = 2)
    private BigDecimal precio;
}
