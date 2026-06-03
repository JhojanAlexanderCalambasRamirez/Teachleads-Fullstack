-- =============================================
-- V1: Initial Schema
-- =============================================

CREATE TABLE empresa (
    nit         VARCHAR(20) PRIMARY KEY,
    nombre      VARCHAR(200) NOT NULL,
    direccion   VARCHAR(300) NOT NULL,
    telefono    VARCHAR(20)  NOT NULL
);

CREATE TABLE categoria (
    id     BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE producto (
    codigo          VARCHAR(50)  PRIMARY KEY,
    nombre          VARCHAR(200) NOT NULL,
    caracteristicas TEXT,
    empresa_nit     VARCHAR(20)  NOT NULL REFERENCES empresa(nit) ON DELETE CASCADE
);

CREATE TABLE producto_precio (
    id               BIGSERIAL PRIMARY KEY,
    producto_codigo  VARCHAR(50) NOT NULL REFERENCES producto(codigo) ON DELETE CASCADE,
    moneda           VARCHAR(3)  NOT NULL,
    precio           NUMERIC(15, 2) NOT NULL,
    UNIQUE(producto_codigo, moneda)
);

CREATE TABLE producto_categoria (
    producto_codigo VARCHAR(50) NOT NULL REFERENCES producto(codigo) ON DELETE CASCADE,
    categoria_id    BIGINT      NOT NULL REFERENCES categoria(id) ON DELETE CASCADE,
    PRIMARY KEY (producto_codigo, categoria_id)
);

CREATE TABLE cliente (
    id     BIGSERIAL    PRIMARY KEY,
    nombre VARCHAR(200) NOT NULL,
    correo VARCHAR(200) NOT NULL UNIQUE
);

CREATE TABLE orden (
    id          BIGSERIAL   PRIMARY KEY,
    cliente_id  BIGINT      NOT NULL REFERENCES cliente(id) ON DELETE CASCADE,
    fecha       TIMESTAMP   NOT NULL DEFAULT NOW(),
    estado      VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE'
);

CREATE TABLE orden_producto (
    orden_id        BIGINT      NOT NULL REFERENCES orden(id) ON DELETE CASCADE,
    producto_codigo VARCHAR(50) NOT NULL REFERENCES producto(codigo),
    cantidad        INTEGER     NOT NULL CHECK (cantidad > 0),
    PRIMARY KEY (orden_id, producto_codigo)
);

CREATE TABLE inventario (
    id              BIGSERIAL   PRIMARY KEY,
    empresa_nit     VARCHAR(20) NOT NULL REFERENCES empresa(nit) ON DELETE CASCADE,
    producto_codigo VARCHAR(50) NOT NULL REFERENCES producto(codigo) ON DELETE CASCADE,
    cantidad        INTEGER     NOT NULL DEFAULT 0 CHECK (cantidad >= 0),
    UNIQUE(empresa_nit, producto_codigo)
);

CREATE TABLE usuario (
    id             BIGSERIAL    PRIMARY KEY,
    correo         VARCHAR(200) NOT NULL UNIQUE,
    password_hash  VARCHAR(255) NOT NULL,
    nombre         VARCHAR(200) NOT NULL,
    rol            VARCHAR(20)  NOT NULL CHECK (rol IN ('ADMIN', 'EXTERNO')),
    activo         BOOLEAN      NOT NULL DEFAULT TRUE
);

-- Indexes
CREATE INDEX idx_producto_empresa ON producto(empresa_nit);
CREATE INDEX idx_orden_cliente ON orden(cliente_id);
CREATE INDEX idx_inventario_empresa ON inventario(empresa_nit);
