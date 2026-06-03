# Prueba Técnica TechLeads — Full Stack Java Spring Boot + Vue.js

Sistema de gestión empresarial con inventario, productos multi-moneda y envío de reportes PDF por email.

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
| Base de datos | PostgreSQL 15, Flyway (migraciones) |
| Autenticación | JWT + BCrypt |
| PDF | iText 8 |
| Email | JavaMailSender (Gmail SMTP) |
| Frontend | Vue 3, Quasar Framework 2, Pinia, TypeScript |
| HTTP Client | Axios |
| Tests | JUnit 5, Mockito |

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

### Opción B — Con Docker (solo para la base de datos)

| Herramienta | Versión mínima |
|-------------|---------------|
| Docker Desktop | 24 |
| Java JDK | 17 |
| Maven | 3.8 |
| Node.js | 20 |

---

## Instalación y ejecución

### 1. Clonar el repositorio

**Mac / Linux:**
```bash
git clone https://github.com/JhojanAlexanderCalambasRamirez/Techleads-Fullstack.git
cd prueba-tecnica-TechLeads
```

**Windows (PowerShell):**
```powershell
git clone https://github.com/JhojanAlexanderCalambasRamirez/Techleads-Fullstack.git
cd prueba-tecnica-TechLeads
```

---

### 2. Base de datos PostgreSQL

#### Opción A — PgAdmin4 (manual)
1. Abre PgAdmin4
2. Click derecho en **Databases** → **Create** → **Database**
3. Nombre: `techleads_db` → **Save**

#### Opción B — Docker (recomendado, cross-platform)
```bash
docker-compose up -d
```
Esto levanta PostgreSQL 15 en el puerto 5432 automáticamente.

---

### 3. Configurar variables de entorno

**Mac / Linux** — en la terminal antes de arrancar el backend:
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

**Windows (CMD):**
```cmd
set DB_USERNAME=postgres
set DB_PASSWORD=postgres
set MAIL_USERNAME=tu_correo@gmail.com
set MAIL_PASSWORD=tu_app_password_16_letras
```

> **Cómo obtener App Password de Gmail:**
> 1. https://myaccount.google.com/apppasswords
> 2. Requiere verificación en 2 pasos activa
> 3. Generar contraseña para "Correo" → copiar 16 letras sin espacios

---

### 4. Backend

**Mac / Linux:**
```bash
cd Backend
mvn spring-boot:run
```

**Windows (PowerShell / CMD):**
```powershell
cd Backend
mvn spring-boot:run
```

Flyway ejecuta las migraciones automáticamente al iniciar.
Espera: `Started PruebaTecnicaApplication`

Backend disponible en: **http://localhost:8080**

---

### 5. Frontend

**Mac / Linux:**
```bash
cd Frontend
npm install
npm run dev
```

**Windows (PowerShell / CMD):**
```powershell
cd Frontend
npm install
npm run dev
```

Frontend disponible en: **http://localhost:9000**

> El proxy del devServer redirige `/api` → `http://localhost:8080` automáticamente.

---

## Matar procesos (si hay conflicto de puertos)

**Mac / Linux:**
```bash
kill -9 $(lsof -ti :8080)   # matar backend
kill -9 $(lsof -ti :9000)   # matar frontend
```

**Windows (PowerShell):**
```powershell
# Ver qué usa el puerto
netstat -ano | findstr :8080

# Matar por PID (reemplaza 1234 con el PID real)
taskkill /PID 1234 /F
```

**Windows (un solo comando):**
```powershell
$pid8080 = (netstat -ano | findstr :8080 | Select-Object -First 1).Split()[-1]
if ($pid8080) { taskkill /PID $pid8080 /F }
```

---

## Arquitectura — Clean Architecture (Hexagonal)

```
Backend/src/main/java/com/techleads/
├── domain/
│   ├── model/          <- Entidades puras Java (sin dependencias de framework)
│   └── port/           <- Interfaces de repositorios (contratos)
├── application/
│   ├── usecase/        <- Lógica de negocio por dominio
│   └── dto/            <- Request / Response DTOs
└── infrastructure/
    ├── persistence/    <- JPA entities + Spring Data + adapters
    ├── web/            <- REST controllers + exception handler
    ├── security/       <- JWT filter + Spring Security
    ├── pdf/            <- Generación PDF (iText 8)
    ├── email/          <- Envío email (JavaMailSender)
    └── config/         <- Beans de configuración
```

**Principios aplicados:** SOLID, inversión de dependencias (domain sin dependencias de Spring/JPA), alta cohesión, bajo acoplamiento.

---

## Modelo de base de datos

```
empresa          (nit PK, nombre, direccion, telefono)
categoria        (id PK, nombre)
producto         (codigo PK, nombre, caracteristicas, empresa_nit FK)
producto_precio  (id PK, producto_codigo FK, moneda, precio)     <- multi-divisa
producto_categoria (producto_codigo FK, categoria_id FK)         <- N:M
cliente          (id PK, nombre, correo)
orden            (id PK, cliente_id FK, fecha, estado)
orden_producto   (orden_id FK, producto_codigo FK, cantidad)     <- N:M
inventario       (id PK, empresa_nit FK, producto_codigo FK, cantidad)
usuario          (id PK, correo, password_hash, nombre, rol, activo)
```

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
| POST | `/api/productos` | ADMIN |
| PUT | `/api/productos/{codigo}` | ADMIN |
| DELETE | `/api/productos/{codigo}` | ADMIN |

### Inventario
| Método | Endpoint | Parámetros | Acceso |
|--------|----------|------------|--------|
| GET | `/api/inventario` | `?empresaNit=` (opcional) | ADMIN |
| POST | `/api/inventario` | body: `{empresaNit, productoCodigo, cantidad}` | ADMIN |
| DELETE | `/api/inventario/{id}` | — | ADMIN |
| GET | `/api/inventario/pdf` | `?ids=1,2,3` o `?empresaNit=` (opcionales) | ADMIN |
| POST | `/api/inventario/enviar-pdf` | body: `{destinatario, ids?, empresaNit?}` | ADMIN |

**Lógica de filtrado PDF/Email (prioridad):**
1. Si `ids` presentes → usa solo esos registros
2. Si `empresaNit` presente → filtra por empresa
3. Sin parámetros → exporta todo el inventario

### Categorías
| Método | Endpoint | Acceso |
|--------|----------|--------|
| GET | `/api/categorias` | Autenticado |
| POST | `/api/categorias` | ADMIN |

---

## Roles de usuario

| Funcionalidad | ADMIN | EXTERNO |
|--------------|:-----:|:-------:|
| Ver empresas | Si | Si |
| Crear / editar / eliminar empresa | Si | No |
| Gestionar productos | Si | No |
| Gestionar inventario | Si | No |
| Descargar PDF inventario | Si | No |
| Enviar PDF por email | Si | No |

---

## Pruebas unitarias

```bash
cd Backend

# Ejecutar todos los tests
mvn test

# Test específico
mvn test -Dtest=EmpresaUseCaseTest
mvn test -Dtest=AuthUseCaseTest
mvn test -Dtest=InventarioUseCaseTest
```

Cobertura: 12 casos de prueba en 3 clases (use cases: Empresa, Auth, Inventario).

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
# Artefactos en: Frontend/dist/spa/
```

---

## Decisiones técnicas

| Decisión | Justificación |
|----------|--------------|
| Clean Architecture | Domain completamente aislado, testeable sin BD |
| JWT stateless | Sin sesiones en servidor, escalable horizontalmente |
| Flyway | Migraciones versionadas, reproducibles en cualquier entorno |
| BCrypt strength 12 | Balance seguridad / performance en autenticación |
| Tabla `producto_precio` normalizada | Soporte N monedas por producto sin columnas dinámicas |
| Pinia como store | Reactividad granular, mejor DX vs Vuex en Vue 3 |
| Proxy devServer | Frontend desacoplado del backend, sin CORS en desarrollo |
| Selección granular en inventario | PDF/email con selección de filas individuales, por empresa, o todo |
