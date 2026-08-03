# Jade Regent — Sistema de Relaciones de Campaña

Aplicación full stack para gestionar relaciones entre **Personajes Jugadores (PJ)** y **NPCs** en una campaña de rol (en este caso, _El Regente de Jade_). El proyecto está dividido en:

- **Backend** en Spring Boot (API REST + persistencia MariaDB gestionada por Flyway).
- **Frontend** en React + Vite + Tailwind.
- **Datos estáticos** e imágenes en `data/`.

---

## 1) Objetivo funcional

El sistema permite:

- Crear y editar PJs.
- Importar NPCs desde JSON.
- Crear relaciones PJ ↔ NPC.
- Registrar interacciones positivas/negativas.
- Subir o bajar nivel de relación.
- Seleccionar ventajas desbloqueables por nivel.
- Consultar matriz global de relaciones.
- Exportar e importar backup lógico completo.

Está pensado para usarse durante sesiones de campaña, tanto por jugadores como por máster.

---

## 2) Arquitectura del proyecto

```text
jaderegent/
├── src/main/java/com/opipo/jaderegent/
│   ├── domain/              # Modelo de dominio + contratos de repositorio
│   ├── application/usecase/ # Casos de uso
│   └── infrastructure/      # REST, DTOs, configuración web
├── src/main/resources/
│   ├── application.properties
│   └── db/migration/          # Migraciones Flyway
├── config/                    # Configuración local ignorada por Git
├── frontend/                # React + Vite + Tailwind
├── data/                    # Imágenes y catálogos JSON
└── docs/                    # Documentación funcional y técnica
```

El backend implementa un estilo hexagonal simplificado:

- **Domain:** entidades (`PJ`, `NPC`, `Relacion`, `Interaccion`, `Ventaja`, etc.).
- **Application:** orquestación de reglas mediante casos de uso.
- **Infrastructure:** controladores REST y configuración HTTP.

---

## 3) Requisitos

### Backend

- **Java 21** (configurado en Maven).
- Maven Wrapper (`./mvnw`) incluido.

### Frontend

- El proyecto puede usar Node local gestionado por `frontend-maven-plugin` (Node `v20.10.0`) o Node global.

---

## 4) Puesta en marcha (desarrollo)

### Opción A — Backend + frontend por separado (recomendado para iterar UI)

1. Levantar backend:

```bash
./mvnw spring-boot:run
```

2. En otra terminal, levantar frontend:

```bash
cd frontend
npm install
npm run dev
```

3. Abrir la URL de Vite (normalmente `http://localhost:5173`).

> El frontend llama a la API con rutas relativas `/api/...`; en desarrollo usa proxy configurado en Vite.

### Opción B — Flujo Maven para instalar Node local

```bash
./mvnw frontend:install-node-and-npm
./mvnw frontend:npm@install
```

En Windows existe `dev.ps1` para invocar el Node local de `target/node`.

---

## 5) Configuración principal

El backend usa las propiedades de `src/main/resources/application.properties`:

- Host y puerto: `0.0.0.0:8080`.
- Base de datos: MariaDB. El perfil `local` se activa por defecto y carga sus credenciales
  desde `config/application-local.properties` (ignorado por Git y externo al artefacto). En despliegues,
  `SPRING_PROFILES_ACTIVE` (o una propiedad equivalente) puede activar otro perfil y sus
  propiedades de conexión.
- Migraciones: Flyway se ejecuta al arrancar; `spring.jpa.hibernate.ddl-auto=validate` valida el esquema.
- JSON de salida en `snake_case`.

Además, `/data/**` se sirve como recurso estático desde la carpeta local `data/` (imágenes de PJ/NPC).

---

## 6) API REST (resumen)

### PJs (`/api/pj`)

- `GET /api/pj` — lista de PJs.
- `POST /api/pj` — crear PJ.
- `GET /api/pj/{id}` — detalle PJ.
- `PUT /api/pj/{id}` — actualizar PJ.
- `DELETE /api/pj/{id}` — eliminar PJ.
- `GET /api/pj/{id}/printable-advantages` — ventajas imprimibles del PJ.

### NPCs (`/api/npc`)

- `GET /api/npc` — lista NPCs.
- `POST /api/npc/import` — importar NPC desde JSON (multipart).
- `GET /api/npc/{id}` — detalle NPC.
- `PUT /api/npc/{id}` — editar NPC.
- `DELETE /api/npc/{id}` — eliminar NPC.

### Relaciones (`/api/relaciones`)

- `GET /api/relaciones/matrix` — matriz global de relaciones.
- `POST /api/relaciones` — crear relación PJ/NPC.
- `POST /api/relaciones/{relacionId}/interacciones` — registrar interacción.
- `PATCH /api/relaciones/{relacionId}/nivel?increment=true|false` — subir/bajar nivel.
- `POST /api/relaciones/{relacionId}/ventajas/{ventajaId}` — seleccionar ventaja.
- `DELETE /api/relaciones/{relacionId}` — eliminar relación.

### Backup (`/api/backup`)

- `GET /api/backup` — exporta backup lógico.
- `POST /api/backup/restore` — restaura backup lógico.

> Ver especificación ampliada con ejemplos en `docs/API.md`.

---

## 7) Frontend: vistas principales

- Pantalla de selección de rol.
- Dashboard principal.
- Alta y gestión de PJs.
- Catálogo y detalle de NPCs.
- Árbol de ventajas de NPC.
- Matriz de relaciones (máster).
- Importación de NPCs (máster).
- Impresión de ventajas por PJ.

---

## 8) Datos y persistencia

- **Persistencia relacional:** MariaDB. El esquema inicial está en
  `src/main/resources/db/migration/V1__initial_schema.sql`.
- **Assets:** imágenes en `data/images/pj` y `data/images/npc`.
- **Catálogos:** JSON de ejemplo en `data/catalogos/` y documentación de relaciones en `docs/relaciones/`.

Variables de entorno de base de datos para perfiles no locales (no guardes contraseñas en el repositorio):

| Variable | Ejemplo local | Descripción |
| --- | --- | --- |
| `DB_URL` | `jdbc:mariadb://localhost:3306/gestion_relaciones` | URL JDBC |
| `DB_USERNAME` | `gestion_app` | Usuario de MariaDB |
| `DB_PASSWORD` | *(obligatoria, definida fuera del repositorio)* | Contraseña |

Para desarrollo local no es necesario exportar variables manualmente: `./mvnw spring-boot:run`
usa el perfil `local` y `config/application-local.properties`. En despliegues que activen otro perfil,
define `DB_URL`, `DB_USERNAME` y `DB_PASSWORD` mediante el gestor de secretos del entorno.

### Preparación administrativa de MariaDB

Ejecuta estas sentencias con una cuenta administrativa y sustituye los marcadores por valores
gestionados fuera del repositorio:

```sql
CREATE DATABASE gestion_relaciones
  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'gestion_app'@'%' IDENTIFIED BY '<define-fuera-del-repositorio>';
GRANT SELECT, INSERT, UPDATE, DELETE, CREATE, ALTER, INDEX, REFERENCES
  ON gestion_relaciones.* TO 'gestion_app'@'%';
FLUSH PRIVILEGES;
```

Configura después `DB_URL`, `DB_USERNAME=gestion_app` y la contraseña real en
`DB_PASSWORD` mediante el gestor de secretos del entorno. Flyway creará y validará su tabla de
historial y aplicará `V1__initial_schema.sql` durante el primer arranque.

Recomendación: incluir `data/` en la estrategia de backup operativo del entorno donde se despliegue.

---

## 9) Testing y calidad

Backend:

```bash
./mvnw test
```

Frontend:

```bash
cd frontend
npm run lint
npm run build
```

---

## 10) Documentación adicional

- `docs/API.md` — endpoints, payloads y ejemplos de uso.
- `docs/GUIA_OPERATIVA.md` — operación diaria, backups, restauración y troubleshooting.
- `docs/` contiene además documentación histórica/versionada del proyecto.

---

## 11) Mejoras sugeridas (roadmap corto)

- Añadir autenticación/autorización para distinguir jugador y máster en backend.
- Ampliar las migraciones Flyway con cambios de esquema futuros.
- Añadir tests de integración REST con datos semilla.
- Automatizar CI (test + lint + build) y release.

