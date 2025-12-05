---
trigger: always_on
---

# Documento de Definición Técnica
### Sistema de Relaciones — Campaña “El Regente de Jade”
**Versión:** 1.3  
**Autor:** Alberto Cebrián  
**Base package:** com.opipo  
**Fecha:** Octubre de 2025

---

## 1. Propósito del documento

Este documento define el **diseño técnico** del sistema de relaciones entre Personajes Jugadores (PJ) y Personajes No Jugadores (NPC) de la campaña *El Regente de Jade*.  
Establece la estructura de datos, entidades del dominio, repositorios, reglas de generación de identificadores, flujos de uso y configuración general para su implementación en **Java + Spring Boot**, bajo una arquitectura **hexagonal (Ports & Adapters)**.

Su finalidad es permitir el desarrollo coherente y mantenible del sistema, garantizando consistencia entre la capa de dominio, persistencia, lógica de aplicación y capa REST.

---

## 2. Contexto y arquitectura general

### 2.1 Objetivo funcional
El sistema permite a los jugadores visualizar y gestionar su nivel de relación con distintos NPC, seleccionar ventajas según su progreso, y al máster controlar la evolución de dichas relaciones, registrar interacciones, y mantener la trazabilidad de las acciones.

### 2.2 Tecnologías
- **Lenguaje:** Java 23  
- **Framework:** Spring Boot  
- **Persistencia:** Spring Data JPA + H2 (modo archivo)  
- **Frontend:** React + Tailwind (embebido)  
- **Serialización JSON:** snake_case mediante Jackson  
- **Arquitectura:** Hexagonal (Ports & Adapters)

### 2.3 Estructura de paquetes
```
com.opipo
├─ domain                 // Núcleo del dominio
│  ├─ model               // Entidades JPA
│  ├─ repository          // Interfaces de repositorios (ports)
│  └─ service             // Reglas de dominio
├─ application            // Casos de uso
│  └─ usecase
├─ infrastructure         // Adaptadores técnicos
│  ├─ persistence         // Implementaciones Spring Data
│  ├─ web                 // Controladores REST + DTOs
│  ├─ init                // Inicialización (catálogos y PJs)
│  ├─ config              // Configuración de Jackson, CORS, etc.
│  └─ files               // Lectura/escritura de JSON
└─ shared                 // Utilidades, validadores, errores comunes
```

---

## 3. Entidades del dominio
A continuación se describen las entidades persistentes, sus atributos, tipos de datos y relaciones.  
Todos los **UUID** son **autogenerados** por la capa de persistencia.

### 3.1. PJ (Jugador)
Representa a un personaje jugador de la campaña.

| Atributo | Tipo | Descripción |
|-----------|------|-------------|
| `pjId` | UUID *(autogenerado)* | Identificador único del PJ. |
| `nombreDisplay` | String | Nombre visible del personaje (único). |
| `notaOpcional` | String | Descripción o comentario libre. |
| `imagenUrl` | String | Ruta local a la imagen (`/data/images/pj/...`). |
| `fechaCreacion` | Timestamp | Fecha y hora de creación. |

**Relaciones:**  
- 1 PJ → N Relaciones (`RELACION`)

---

### 3.2. NPC (Personaje no jugador)
Define un personaje no jugador con su árbol de ventajas.

| Atributo | Tipo | Descripción |
|-----------|------|-------------|
| `npcId` | String | Identificador único (coincide con el nombre del archivo JSON). |
| `nombre` | String | Nombre visible (único). |
| `descripcionLarga` | Texto | Descripción narrativa del NPC. |
| `nivelMaximo` | Integer | Nivel máximo de relación (3, 5 o 10). |
| `imagenUrl` | String | Ruta local de la imagen (`/data/images/npc/...`). |
| `fechaImportacion` | Timestamp | Fecha de la última importación del catálogo JSON. |

**Relaciones:**  
- 1 NPC → N Ventajas (`VENTAJA`)  
- 1 NPC → N Relaciones (`RELACION`)

---

### 3.3. Ventaja
Elemento individual del árbol de ventajas de un NPC.

| Atributo | Tipo | Descripción |
|-----------|------|-------------|
| `ventajaId` | String *(autogenerado si no se especifica)* | Identificador único global. Se genera automáticamente combinando el `npcId` y el nombre de la ventaja, normalizado en minúsculas (`npcid_nombre_ventaja`). Ejemplo: `ameiko_cancion_de_animo`. |
| `npc` | NPC | NPC al que pertenece. |
| `nombre` | String | Nombre de la ventaja. |
| `descripcionLarga` | Texto | Descripción detallada. |
| `minNivelRelacion` | Integer | Nivel mínimo necesario para poder elegirla. |
| `prerequisitos` | Lista de Strings | Identificadores (`ventajaId`) de ventajas requeridas como prerequisitos. |

**Relaciones:**  
- N Ventajas → 1 NPC  

**Generación automática de IDs:**  
Durante la importación del catálogo JSON, si una ventaja no tiene `ventaja_id`, el sistema lo genera automáticamente aplicando la regla:  
```
ventajaId = (npcId + "_" + nombreVentajaNormalizado)
```
Donde `nombreVentajaNormalizado` se transforma a minúsculas, sin tildes, con espacios convertidos en guiones bajos y caracteres especiales eliminados.

Ejemplo:  
> NPC: `ameiko`  
> Nombre: `Canción de Ánimo`  
> → `ventajaId = "ameiko_cancion_de_animo"`

---

### 3.4. Relacion (PJ–NPC)
Define el vínculo entre un PJ y un NPC.

| Atributo | Tipo | Descripción |
|-----------|------|-------------|
| `relacionId` | UUID *(autogenerado)* | Identificador único. |
| `pj` | PJ | PJ asociado. |
| `npc` | NPC | NPC asociado. |
| `nivelActual` | Integer | Nivel de relación (0 a nivel máximo). |
| `pendienteEleccion` | Boolean | Indica si el PJ debe elegir una ventaja. |
| `consistente` | Boolean | Indica si la relación es coherente con el árbol actual. |
| `contadorInteracciones` | Integer | Total neto de interacciones positivas y negativas. |
| `ultimaActualizacionTs` | Timestamp | Fecha del último cambio. |

**Relaciones:**  
- 1 Relación → N Interacciones  
- 1 Relación → N Selecciones de Ventajas  
- 1 Relación → N Snapshots de Selecciones

---

### 3.5. Interaccion
Registra cada interacción entre un PJ y un NPC.

| Atributo | Tipo | Descripción |
|-----------|------|-------------|
| `interaccionId` | UUID *(autogenerado)* | Identificador único. |
| `relacion` | Relacion | Relación correspondiente. |
| `tipo` | Enum (POSITIVA, NEGATIVA) | Tipo de interacción. |
| `valor` | Integer | Valor (+1 o -1 por defecto). |
| `nota` | String | Comentario opcional. |
| `ts` | Timestamp | Fecha y hora del evento. |
| `usuario` | String | Autor (habitualmente “master”). |

---

### 3.6. SeleccionVentaja
Ventajas elegidas por un PJ en cada nivel de relación.

| Atributo | Tipo | Descripción |
|-----------|------|-------------|
| `seleccionId` | UUID *(autogenerado)* | Identificador único. |
| `relacion` | Relacion | Relación a la que pertenece. |
| `nivelRelacion` | Integer | Nivel en el que se eligió. |
| `ventaja` | Ventaja | Ventaja seleccionada. |
| `ts` | Timestamp | Fecha de la elección. |
| `usuario` | String | Quién realizó la acción. |

---

### 3.7. SnapshotSelecciones
Versionado completo de las ventajas elegidas.

| Atributo | Tipo | Descripción |
|-----------|------|-------------|
| `snapshotId` | UUID *(autogenerado)* | Identificador único. |
| `relacion` | Relacion | Relación a la que pertenece. |
| `version` | Integer | Número incremental de versión. |
| `ts` | Timestamp | Fecha de creación del snapshot. |
| `usuario` | String | Autor. |

---

### 3.8. SnapshotSeleccionItem
Ventajas incluidas dentro de un snapshot.

| Atributo | Tipo | Descripción |
|-----------|------|-------------|
| `id` | UUID *(autogenerado)* | Identificador único. |
| `snapshot` | SnapshotSelecciones | Snapshot asociado. |
| `ventaja` | Ventaja | Ventaja incluida. |
| `nivelRelacion` | Integer | Nivel de relación correspondiente. |

---

### 3.9. LogAccion
Registro auditable de operaciones importantes.

| Atributo | Tipo | Descripción |
|-----------|------|-------------|
| `logId` | UUID *(autogenerado)* | Identificador único. |
| `ts` | Timestamp | Fecha y hora. |
| `usuario` | String | Autor de la acción. |
| `accion` | String | Tipo de acción (ej. “IMPORTAR_NPC”, “SUBIR_NIVEL”). |
| `detalle` | Texto | Información descriptiva adicional. |

---

## 4. Repositorios
Todos los repositorios extienden `JpaRepository<Entity, Id>` y añaden métodos específicos según las necesidades del dominio.

| Repositorio | Métodos adicionales |
|--------------|--------------------|
| **PJ** | `existsByNombreDisplay(nombreDisplay)` — Verifica si existe un PJ con ese nombre.<br>`findByNombreDisplay(nombreDisplay)` — Busca un PJ por nombre. |
| **NPC** | `existsByNombre(nombre)` — Comprueba si existe un NPC con ese nombre.<br>`findByNombre(nombre)` — Busca un NPC por nombre. |
| **Ventaja** | `findByNpcNpcId(npcId)` — Lista todas las ventajas de un NPC. |
| **Relacion** | `findByPjPjIdAndNpcNpcId(pjId, npcId)` — Recupera una relación específica.<br>`findByPjPjId(pjId)` — Lista relaciones de un PJ.<br>`findByNpcNpcId(npcId)` — Lista relaciones de un NPC. |
| **Interaccion** | `findByRelacionRelacionIdOrderByTsDesc(relacionId)` — Devuelve interacciones de una relación, ordenadas por fecha. |
| **SeleccionVentaja** | `findByRelacionRelacionIdAndNivelRelacion(relacionId, nivel)` — Busca la ventaja de un nivel concreto.<br>`findByRelacionRelacionId(relacionId)` — Lista todas las ventajas elegidas. |
| **SnapshotSelecciones** | `findByRelacionRelacionIdOrderByVersionDesc(relacionId)` — Lista snapshots de una relación (más recientes primero). |
| **SnapshotSeleccionItem** | `findBySnapshotSnapshotId(snapshotId)` — Lista ventajas de un snapshot. |
| **LogAccion** | `findByUsuarioOrderByTsDesc(usuario)` — Lista acciones de un usuario en orden cronológico inverso. |

---

## 5. Casos de uso principales

| Caso de uso | Descripción |
|--------------|-------------|
| **Crear PJ** | Crea un nuevo PJ y genera relaciones nivel 0 con los NPC existentes. |
| **Importar NPC** | Carga un JSON, valida prerequisitos, genera automáticamente `ventajaId` si falta, y reemplaza el NPC. |
| **Eliminar NPC** | Elimina el NPC y sus entidades dependientes (relaciones, selecciones, interacciones, snapshots). |
| **Registrar interacción** | Añade una interacción y actualiza el contador neto. |
| **Subir nivel de relación** | Incrementa el nivel, marca la relación como pendiente y guarda snapshot. |
| **Elegir ventaja** | Registra una ventaja elegida, valida prerequisitos y guarda snapshot. |
| **Revertir snapshot** | Restaura una configuración anterior de ventajas sin alterar el nivel actual. |
| **Exportar/Importar estado** | Guarda o carga el estado completo de la aplicación. |

### 5.1 Verificación de inconsistencias (tras cambios del árbol)
Al **importar o modificar** el árbol de ventajas de un NPC, el sistema ejecuta un **cálculo de inconsistencias** sobre todas las relaciones afectadas.  
Este cálculo **revisa que todas las ventajas ya adquiridas por los PJs continúen cumpliendo sus prerequisitos** vigentes.

---

## 6. Configuración y rutas

| Recurso | Ruta |
|----------|------|
| Base de datos H2 | `./data/database.mv.db` |
| Imágenes de PJs | `./data/images/pj/` |
| Imágenes de NPCs | `./data/images/npc/` |
| Catálogos JSON | `./data/catalogos/` |

---

## 7. Flujo de uso resumido
1. El máster importa un JSON de NPC.  
2. Se autogeneran `ventajaId` cuando faltan.  
3. Se crean relaciones nivel 0 para todos los PJs.  
4. El máster registra interacciones positivas o negativas.  
5. Se sube el nivel de relación y queda pendiente elegir ventaja.  
6. El jugador elige una ventaja elegible y se guarda snapshot.  
7. Tras cambios de árbol, el sistema ejecuta **verificación de inconsistencias** y, si procede, marca relaciones como pendientes y elimina selecciones inválidas.  
8. Todas las acciones quedan registradas en `LogAccion`.

---

**Versión 1.3 — Documento de Definición Técnica actualizado.**  
Incluye la verificación de inconsistencias tras cambios en el árbol de ventajas, con ejemplo y efectos sobre relaciones y selecciones.

