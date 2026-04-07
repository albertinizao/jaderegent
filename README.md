# JadeRegent

Aplicación web para gestionar relaciones entre personajes jugadores (PJs) y personajes no jugadores (NPCs) en la campaña de Pathfinder **El Regente de Jade**.

El proyecto está orientado a partidas en red local: el máster administra NPCs, interacciones y progresión; los jugadores consultan y eligen ventajas desde navegador.

## Características principales

- Gestión de PJs (alta, consulta, edición y borrado).
- Gestión de NPCs e importación desde JSON.
- Creación y seguimiento de relaciones PJ–NPC.
- Registro de interacciones y subida/bajada de nivel de relación.
- Selección de ventajas por relación.
- Matriz de relaciones para visión global.
- Exportación y restauración de backups completos.

## Arquitectura

- **Backend:** Java 21 + Spring Boot 3 + Spring Data JPA.
- **Base de datos:** H2 en modo archivo (`./data/database`).
- **Frontend:** React + Vite + Tailwind CSS.
- **Comunicación:** API REST bajo `/api`.

## Estructura del repositorio

```text
.
├── src/                  # Backend Spring Boot
├── frontend/             # Aplicación React
├── data/                 # Datos locales e imágenes
├── docs/                 # Documentación funcional y técnica
└── pom.xml               # Configuración Maven
```

## Requisitos

- **JDK 21**
- **Maven Wrapper** (incluido: `./mvnw`)
- **Node.js 20+** y **npm** (para ejecutar frontend en desarrollo)

## Puesta en marcha (desarrollo)

### 1) Backend

Desde la raíz del proyecto:

```bash
./mvnw spring-boot:run
```

Backend disponible en:

- API: `http://localhost:8080`
- Consola H2: `http://localhost:8080/h2-console`

Credenciales H2 por defecto:

- JDBC URL: `jdbc:h2:file:./data/database`
- Usuario: `sa`
- Contraseña: `password`

### 2) Frontend

En otra terminal:

```bash
cd frontend
npm install
npm run dev
```

Vite mostrará la URL de desarrollo (normalmente `http://localhost:5173`).

## Scripts útiles

### Backend

```bash
./mvnw test
./mvnw package
```

### Frontend

```bash
cd frontend
npm run dev
npm run build
npm run lint
npm run preview
```

## Endpoints principales de la API

- `GET /api/pj` — listado de PJs.
- `POST /api/pj` — creación de PJ.
- `GET /api/pj/{id}` — detalle de PJ.
- `GET /api/npc` — listado de NPCs.
- `POST /api/npc/import` — importación de NPC desde JSON.
- `GET /api/relaciones/matrix` — matriz de relaciones.
- `POST /api/relaciones` — crear relación PJ–NPC.
- `POST /api/relaciones/{relacionId}/interacciones` — registrar interacción.
- `POST /api/relaciones/{relacionId}/ventajas/{ventajaId}` — seleccionar ventaja.
- `GET /api/backup` — exportar backup.
- `POST /api/backup/restore` — restaurar backup.

## Documentación

La carpeta `docs/` contiene documentación funcional y técnica (visión, arquitectura, despliegue, pruebas, etc.). Recomendado empezar por:

- `docs/vision_y_alcance_v_1_0.md`
- `docs/arquitectura_tecnica_v_1_0.md`
- `docs/despliegue_y_configuracion_v_1_2.md`

## Estado del proyecto

Proyecto en evolución para uso interno de campaña. El foco está en soporte local y fluidez en mesa de juego.
