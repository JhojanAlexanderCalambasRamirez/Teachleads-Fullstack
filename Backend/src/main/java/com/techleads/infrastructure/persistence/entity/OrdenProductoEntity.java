package com.techleads.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "orden_producto")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@IdClass(OrdenProductoId.class)
public class OrdenProductoEntity {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orden_id")
    private OrdenEntity orden;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_codigo")
    private ProductoEntity producto;

    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;
}
