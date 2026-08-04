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

- El proyecto puede usar Node local gestionado por `frontend-maven-plugin` (Node `v20.19.0`) o Node global.

---

## 4) Puesta en marcha

### 4.1. Desarrollo con recarga automática (dos procesos)

Este es el flujo recomendado para desarrollar la interfaz. Vite proporciona hot reload,
por lo que se mantienen dos procesos separados.

**Terminal 1 — backend Spring Boot:**

```powershell
cd C:\Users\Alberto\workspace\jaderegent
.\mvnw.cmd spring-boot:run
```

**Terminal 2 — frontend Vite:**

```powershell
cd C:\Users\Alberto\workspace\jaderegent
.\dev.ps1 npm run dev
```

Abrir `http://localhost:5173`. El proxy de Vite reenvía `/api/**` y `/data/**`
al backend en `http://localhost:8080`.

En Linux/macOS, si se usa Node instalado globalmente:

```bash
./mvnw spring-boot:run                 # terminal 1
cd frontend && npm ci && npm run dev   # terminal 2
```

### 4.2. Producción o prueba del JAR (un único proceso)

El build de Maven compila React/Vite y copia `frontend/dist` dentro del JAR de Spring Boot.
Node, npm y Vite solo son necesarios durante la compilación; no son necesarios en runtime.

Generar el artefacto:

```powershell
cd C:\Users\Alberto\workspace\jaderegent
.\mvnw.cmd clean package
```

Arrancar únicamente Spring Boot, sin ejecutar Vite:

```powershell
java -jar target\jaderegent-0.0.1-SNAPSHOT.jar
```

Abrir `http://localhost:8080/`. La misma aplicación sirve la SPA, sus assets,
`/api/**` y `/data/**`.

En Linux/macOS:

```bash
./mvnw clean package
java -jar target/jaderegent-0.0.1-SNAPSHOT.jar
```

Para usar configuración externa:

```bash
java -jar target/jaderegent-0.0.1-SNAPSHOT.jar \
  --spring.config.additional-location=file:./config/
```

En este modo no se debe arrancar `npm run dev`, `vite preview` ni un segundo servidor frontend.

### 4.3. Flujo Maven para gestionar Node local

El `pom.xml` ejecuta automáticamente, durante `package`, estas fases:

```text
instalar Node/npm → npm ci → npm run build → copiar frontend/dist → empaquetar JAR
```

También se puede invocar el Node descargado en `target/node` mediante `dev.ps1`:

```powershell
.\dev.ps1 npm ci
.\dev.ps1 npm run build
```

Para el flujo habitual no es necesario ejecutar estos comandos manualmente: `mvn package`
los ejecuta en el orden correcto.

### 4.4. Despliegue con Docker

La imagen Docker es únicamente de runtime: ejecuta el JAR ya compilado con Java 21,
sin Maven, Node ni Vite. El directorio `data/` se incluye dentro de la imagen, por lo
que los cambios de imágenes o catálogos requieren reconstruirla.

Primero genera el JAR:

```powershell
.\mvnw.cmd clean package
```

Construye la imagen desde la raíz del proyecto:

```powershell
docker build -t jaderegent:local .
```

#### MariaDB instalada en la máquina anfitriona

En Docker Desktop para Windows, `localhost` dentro del contenedor apunta al propio
contenedor. Para acceder a MariaDB instalada en Windows se debe utilizar
`host.docker.internal`:

```powershell
docker run -d `
  --name jaderegent `
  -p 8080:8080 `
  -e DB_URL="jdbc:mariadb://host.docker.internal:3306/gestion_relaciones" `
  -e DB_USERNAME="gestion_app" `
  -e DB_PASSWORD="<contraseña>" `
  jaderegent:local
```

No se deben guardar contraseñas en el `Dockerfile` ni en el repositorio. También se
puede utilizar un archivo de entorno fuera del repositorio:

```powershell
docker run -d `
  --name jaderegent `
  -p 8080:8080 `
  --env-file .env.docker `
  jaderegent:local
```

El archivo debe definir `DB_URL`, `DB_USERNAME` y `DB_PASSWORD`. Si MariaDB se ejecuta
en otro contenedor, usa el nombre del servicio Docker en `DB_URL` (por ejemplo,
`jdbc:mariadb://mariadb:3306/gestion_relaciones`) y conecta ambos contenedores a la
misma red.

#### Errores habituales

- `UnknownHostException: host`: se ha usado literalmente `host` como hostname; debe
  sustituirse por `host.docker.internal`, un nombre de servicio Docker o un DNS/IP real.
- `Connection refused`: MariaDB no está escuchando en una interfaz accesible desde
  Docker, el puerto 3306 está bloqueado por el firewall o el usuario no tiene permisos
  para conexiones desde el origen del contenedor. Revisa `bind-address`, el firewall y
  los permisos del usuario MariaDB.
- `Picked up JAVA_TOOL_OPTIONS`: es un mensaje informativo de la JVM, no el error real.

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

