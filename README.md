# Prueba Técnica TechLeads — Full Stack Java Spring Boot + Vue.js

Sistema de gestión empresarial con inventario, productos multi-moneda y exportación de reportes PDF por email.

---

## Credenciales de acceso

| Perfil | Correo | Contraseña |
|--------|--------|------------|
| Administrador | admin@techleads.com | admin123 |
| Externo | externo@techleads.com | externo123 |

---

## Stack tecnológico

| Capa | Tecnología |
|------|-----------|
| Backend | Java 17, Spring Boot 3.2, Spring Security, Hibernate |
| Base de datos | PostgreSQL 15, Flyway (migraciones versionadas) |
| Autenticación | JWT (JJWT 0.12) + BCrypt strength 12 |
| Transacciones | `@Transactional` / `@Transactional(readOnly=true)` |
| Excepciones | Custom domain exceptions + `@RestControllerAdvice` |
| PDF | iText 8 |
| Email | JavaMailSender (Gmail SMTP) |
| Frontend | Vue 3, Quasar Framework 2, Pinia, TypeScript |
| Arquitectura UI | Atomic Design (molecules + organisms + pages) |
| HTTP Client | Axios (interceptores JWT + 401 handler) |
| Tests | JUnit 5, Mockito — 18 casos en 4 clases |

---

## Prerrequisitos

### Opción A — Sin Docker (instalación manual)

| Herramienta | Versión mínima | Verificar |
|-------------|---------------|-----------|
| Java JDK | 17 | `java -version` |
| Maven | 3.8 | `mvn -version` |
| Node.js | 20 | `node -v` |
| npm | 9 | `npm -v` |
| PostgreSQL | 14 | PgAdmin4 o `psql --version` |

### Opción B — Con Docker (base de datos)

```bash
docker-compose up -d
```

---

## Instalación y ejecución

### 1. Clonar

```bash
git clone https://github.com/JhojanAlexanderCalambasRamirez/Teachleads-Fullstack.git
cd prueba-tecnica-TeachLeads
```

### 2. Base de datos

**PgAdmin4:** crear base de datos `techleads_db`

**Docker:**
```bash
docker-compose up -d
```

### 3. Variables de entorno

**Mac / Linux:**
```bash
export DB_USERNAME=postgres
export DB_PASSWORD=postgres
export MAIL_USERNAME=tu_correo@gmail.com
export MAIL_PASSWORD=tu_app_password_16_letras
```

**Windows (PowerShell):**
```powershell
$env:DB_USERNAME="postgres"
$env:DB_PASSWORD="postgres"
$env:MAIL_USERNAME="tu_correo@gmail.com"
$env:MAIL_PASSWORD="tu_app_password_16_letras"
```

> **App Password Gmail:** https://myaccount.google.com/apppasswords — requiere verificación en 2 pasos.

### 4. Backend

```bash
cd Backend
mvn spring-boot:run
```
Espera: `Started PruebaTecnicaApplication` — backend en **http://localhost:8080**

### 5. Frontend

```bash
cd Frontend
npm install
npm run dev
```
Frontend en **http://localhost:9000** — proxy `/api` → `localhost:8080` configurado.

---

## Matar procesos

**Mac / Linux:**
```bash
kill -9 $(lsof -ti :8080)
kill -9 $(lsof -ti :9000)
```

**Windows:**
```powershell
$pid8080 = (netstat -ano | findstr :8080 | Select-Object -First 1).Split()[-1]
if ($pid8080) { taskkill /PID $pid8080 /F }
```

---

## Arquitectura — Clean Architecture (Hexagonal)

```
Backend/src/main/java/com/techleads/
├── domain/
│   ├── model/          <- Entidades puras Java (0 imports de Spring)
│   ├── port/           <- Interfaces de repositorios (contratos)
│   └── exception/      <- Custom exceptions de dominio (NEW)
├── application/
│   ├── usecase/        <- Casos de uso con @Transactional (NEW)
│   └── dto/            <- Request / Response DTOs con validaciones Jakarta
└── infrastructure/
    ├── persistence/    <- JPA entities + Spring Data + adapters (Ports & Adapters)
    ├── web/            <- REST controllers + GlobalExceptionHandler
    ├── security/       <- JWT filter + Spring Security config
    ├── pdf/            <- Generación PDF en memoria (iText 8)
    ├── email/          <- Envío email con adjunto (JavaMailSender)
    └── config/         <- SecurityConfig, CORS, BCryptPasswordEncoder
```

### Principios SOLID aplicados

| Principio | Dónde se ve |
|---|---|
| **S** — Single Responsibility | Cada use case gestiona un dominio. `PdfService` solo genera PDF. `EmailService` solo envía. |
| **O** — Open/Closed | Ports como interfaces — nueva implementación sin tocar use cases |
| **L** — Liskov | `EmpresaRepositoryAdapter implements EmpresaRepository` — sustituible |
| **I** — Interface Segregation | Cada port solo expone los métodos que necesita su dominio |
| **D** — Dependency Inversion | Use cases dependen de interfaces (`domain/port`), no de JPA concreto |

---

## Custom Exceptions (domain/exception/)

```
EmpresaNotFoundException.java         → HTTP 404
NitDuplicadoException.java            → HTTP 409
ProductoNotFoundException.java        → HTTP 404
ProductoCodigoDuplicadoException.java → HTTP 409
InventarioItemNotFoundException.java  → HTTP 404
```

`GlobalExceptionHandler` mapea cada excepción de dominio a su HTTP status con `@ExceptionHandler` por tipo. Sin try-catch en lógica de negocio.

---

## Transacciones

Todos los use cases usan `@Transactional` de Spring:

```java
@Transactional                    // operaciones write — garantiza rollback
@Transactional(readOnly = true)   // operaciones read — optimiza connection pool
```

16 anotaciones en `EmpresaUseCase`, `ProductoUseCase` e `InventarioUseCase`.

---

## Atomic Design (Frontend)

```
Frontend/src/components/
├── empresa/
│   ├── EmpresaTable.vue        <- organism: tabla con eventos edit/delete
│   └── EmpresaFormDialog.vue   <- molecule: formulario crear/editar reutilizable
├── inventario/
│   ├── AddInventarioDialog.vue <- molecule: formulario agregar stock
│   └── SendEmailDialog.vue     <- molecule: dialog envío con contexto visual
└── shared/
    └── PageHeader.vue          <- atom: título + slot para acciones
```

`EmpresaPage.vue` pasó de 156 líneas monolíticas a 70 líneas que solo orquestan componentes.

---

## Modelo de base de datos

```
empresa          (nit PK, nombre, direccion, telefono)
categoria        (id PK, nombre UNIQUE)
producto         (codigo PK, nombre, caracteristicas, empresa_nit FK)
producto_precio  (id PK, producto_codigo FK, moneda, precio)     <- multi-divisa
producto_categoria (producto_codigo FK, categoria_id FK)         <- N:M
cliente          (id PK, nombre, correo UNIQUE)
orden            (id PK, cliente_id FK, fecha, estado)
orden_producto   (orden_id FK, producto_codigo FK, cantidad)     <- N:M
inventario       (id PK, empresa_nit FK, producto_codigo FK, cantidad)
usuario          (id PK, correo UNIQUE, password_hash, nombre, rol, activo)
```

### Tablas con UI vs solo modelo ER

| Tabla | UI | Nota |
|---|---|---|
| `empresa` | ✅ | CRUD completo (ADMIN), solo lectura (EXTERNO) |
| `producto` | ✅ | CRUD completo (ADMIN) con precios multi-moneda |
| `producto_precio` | ✅ | Gestionada dentro del formulario de Productos |
| `producto_categoria` | ✅ | Selector múltiple en formulario de Productos |
| `categoria` | ✅ | Pre-seeded, selector en Productos |
| `inventario` | ✅ | CRUD + PDF/email granular (ADMIN) |
| `usuario` | ✅ | Pre-seeded — 2 usuarios (admin y externo) |
| `cliente` | ❌ | Modelo ER requerido por PDF punto f), sin UI |
| `orden` | ❌ | Modelo ER requerido por PDF punto f), sin UI |
| `orden_producto` | ❌ | Modelo ER requerido por PDF punto f), sin UI |

> Las tablas `cliente`, `orden` y `orden_producto` cumplen el requisito del modelo ER del punto f). No tienen UI porque ninguno de los puntos a-d del PDF define vistas para ellas.

---

## Endpoints REST

### Auth
| Método | Endpoint | Acceso |
|--------|----------|--------|
| POST | `/api/auth/login` | Público |

### Empresas
| Método | Endpoint | Acceso |
|--------|----------|--------|
| GET | `/api/empresas` | Autenticado |
| GET | `/api/empresas/{nit}` | Autenticado |
| POST | `/api/empresas` | ADMIN |
| PUT | `/api/empresas/{nit}` | ADMIN |
| DELETE | `/api/empresas/{nit}` | ADMIN |

### Productos
| Método | Endpoint | Acceso |
|--------|----------|--------|
| GET | `/api/productos?empresaNit=` | Autenticado |
| GET | `/api/productos/{codigo}` | Autenticado |
| POST | `/api/productos` | ADMIN |
| PUT | `/api/productos/{codigo}` | ADMIN |
| DELETE | `/api/productos/{codigo}` | ADMIN |

### Inventario
| Método | Endpoint | Parámetros | Acceso |
|--------|----------|------------|--------|
| GET | `/api/inventario` | `?empresaNit=` (opcional) | ADMIN |
| POST | `/api/inventario` | `{empresaNit, productoCodigo, cantidad}` | ADMIN |
| DELETE | `/api/inventario/{id}` | — | ADMIN |
| GET | `/api/inventario/pdf` | `?ids=1,2,3` o `?empresaNit=` | ADMIN |
| POST | `/api/inventario/enviar-pdf` | `{destinatario, ids?, empresaNit?}` | ADMIN |

**Lógica de filtrado (prioridad):** `ids` > `empresaNit` > todo el inventario

### Categorías
| Método | Endpoint | Acceso |
|--------|----------|--------|
| GET | `/api/categorias` | Autenticado |
| POST | `/api/categorias` | ADMIN |

---

## Roles de usuario

| Funcionalidad | ADMIN | EXTERNO |
|--------------|:-----:|:-------:|
| Ver empresas | ✅ | ✅ |
| Crear / editar / eliminar empresa | ✅ | ❌ |
| Gestionar productos | ✅ | ❌ |
| Gestionar inventario | ✅ | ❌ |
| Descargar PDF (todo / empresa / selección) | ✅ | ❌ |
| Enviar PDF por email | ✅ | ❌ |

---

## Pruebas unitarias — 18 casos

```bash
cd Backend

# Todos
mvn test

# Por clase
mvn test -Dtest=EmpresaUseCaseTest      # 5 casos
mvn test -Dtest=AuthUseCaseTest         # 4 casos
mvn test -Dtest=InventarioUseCaseTest   # 3 casos
mvn test -Dtest=ProductoUseCaseTest     # 6 casos

# Con output detallado
mvn test -Dsurefire.useFile=false
```

| Clase | Casos | Qué cubre |
|---|---|---|
| `EmpresaUseCaseTest` | 5 | CRUD empresa, NitDuplicadoException, EmpresaNotFoundException |
| `AuthUseCaseTest` | 4 | Login, credenciales inválidas, usuario inactivo |
| `InventarioUseCaseTest` | 3 | Crear, acumular cantidad, lista vacía |
| `ProductoUseCaseTest` | 6 | CRUD producto, ProductoCodigoDuplicadoException, EmpresaNotFoundException |

Todas son **pruebas unitarias puras con Mockito** — sin Spring context, sin BD, en ~50ms.

---

## Build producción

**Backend:**
```bash
cd Backend
mvn clean package -DskipTests
java -jar target/prueba-tecnica-1.0.0.jar
```

**Frontend:**
```bash
cd Frontend
npm run build
# Artefactos: Frontend/dist/spa/
```

---

## Decisiones técnicas

| Decisión | Justificación |
|----------|--------------|
| Clean Architecture | Domain 100% aislado — testeable sin Spring, sin BD |
| Custom exceptions en `domain/exception/` | Semántica de error en el dominio, no en HTTP |
| `@Transactional(readOnly=true)` en reads | Optimiza connection pool — Hibernate no hace flush ni tracking |
| Atomic Design en frontend | Componentes reutilizables, páginas delgadas que orquestan |
| JWT stateless | Escalable horizontalmente — sin estado en servidor |
| Flyway migraciones | Schema reproducible, versionado y auditable |
| BCrypt strength 12 | 4096 rondas — balance seguridad/performance |
| `producto_precio` normalizada | N monedas sin columnas fijas — extensible sin ALTER TABLE |
| Selección granular inventario | PDF/email: todo / empresa / ítems específicos con checkboxes |
| Proxy devServer Quasar | Frontend desacoplado — sin CORS en desarrollo |
