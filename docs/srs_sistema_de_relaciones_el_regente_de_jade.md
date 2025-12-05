# SRS — Sistema de Relaciones para NPCs (Campaña: *El Regente de Jade*)

**Versión:** 0.7 (Final consolidada)  
**Fecha:** 17/10/2025  
**Stakeholders:** 1 Máster (admin) · 6 Jugadores (PJs)

---

## 1. Objetivo y Alcance
Aplicación web ligera para gestionar los niveles de relación entre **PJs** y **NPCs** en la campaña *El Regente de Jade*.  
Cada **NPC** tiene un **árbol de ventajas**, definido por el máster mediante un archivo JSON cargado en el sistema.  
Cada **PJ–NPC** tiene un **nivel de relación** (máximo 3, 5 o 10 según el NPC).  
En cada nivel alcanzado, el **PJ elige una ventaja** del árbol del NPC (respetando prerequisitos).  
El **máster** registra interacciones positivas o negativas, decide cuándo subir niveles y gestiona el catálogo de NPCs y sus árboles.

Fuera de alcance: autenticación, modo oscuro, traducción, y funcionalidades de otras áreas de campaña.

---

## 2. Roles y Permisos

### Jugador (PJ)
- Puede crear su propio PJ desde la interfaz.  
- Ver sus relaciones con cada NPC (nivel, progreso y ventajas elegidas o pendientes).  
- Elegir ventajas cuando tenga una relación pendiente.  
- Consultar los árboles de ventajas de los NPCs.

### Máster (Admin)
- Crear, editar y eliminar NPCs.  
- Definir o importar árboles de ventajas mediante JSON.  
- Subir o bajar manualmente el nivel de relación entre un PJ y un NPC.  
- Registrar interacciones positivas o negativas.  
- Visualizar el estado de todas las relaciones (matriz PJs × NPCs).  
- Eliminar NPCs (borrado en cascada de relaciones, selecciones e interacciones, conservando logs).  
- Exportar e importar datos y catálogos.  
- Asignar imágenes locales a PJs y NPCs.

> No existe autenticación. Cada usuario elige su modo (PJ o máster) al entrar.

---

## 3. Historias de Usuario

### Máster — Configuración y gestión
1. Crear y editar NPCs con nombre, descripción, nivel máximo e imagen.  
2. Definir y actualizar árboles de ventajas por NPC (nombre, descripción, prerequisitos y nivel mínimo).  
3. Importar y exportar catálogos JSON de NPCs y árboles.  
4. Registrar interacciones positivas o negativas entre PJs y NPCs.  
5. Subir o bajar niveles de relación manualmente.  
6. Ver un tablero con el estado general de las relaciones (niveles, pendientes, interacciones).  
7. Revertir ventajas seleccionadas a una versión anterior (modelo snapshot).  
8. Eliminar NPCs, borrando relaciones asociadas en cascada.

### Jugador — Interacción
9. Crear un PJ propio.  
10. Consultar sus relaciones con todos los NPCs.  
11. Ver el árbol de ventajas de un NPC (con prerequisitos y ventajas bloqueadas).  
12. Elegir una ventaja disponible al subir de nivel.

---

## 4. Requisitos Funcionales

### RF1 — Catálogo de NPCs y Ventajas
- RF1.1: Los NPCs y sus árboles se definen mediante archivos JSON.  
- RF1.2: Cada ventaja incluye `ventaja_id`, `nombre`, `descripcion_larga`, `min_nivel_relacion`, y `prerequisitos` (lista interna de `ventaja_id`).  
- RF1.3: El máster puede cargar imágenes locales (`images/npc/...`) asociadas a cada NPC.  
- RF1.4: Al eliminar un NPC, se borran en cascada todas las relaciones, selecciones y snapshots.

### RF2 — Relaciones PJ–NPC
- RF2.1: Cada PJ y NPC tienen una relación inicial de nivel 0 (sin ventajas).  
- RF2.2: El máster puede subir o bajar el nivel manualmente (de uno en uno).  
- RF2.3: Subir nivel marca la relación como `pendiente_eleccion=true`.  
- RF2.4: Las relaciones inválidas tras una importación se recalculan automáticamente y se marcan como pendientes.  
- RF2.5: Se guarda un contador neto de interacciones (positivas–negativas).  
- RF2.6: Validación automática de rutas de imagen al iniciar (placeholder si falta archivo).

### RF3 — Interacciones
- RF3.1: Registrar interacciones con tipo `{POSITIVA, NEGATIVA}` y valor entero (por defecto ±1).  
- RF3.2: Las interacciones se guardan automáticamente con fecha, hora y usuario.  
- RF3.3: El máster puede añadir notas opcionales.

### RF4 — Selección de Ventajas
- RF4.1: Los PJs pueden elegir una ventaja por nivel alcanzado.  
- RF4.2: Solo se muestran elegibles las ventajas con `min_nivel_relacion ≤ nivel_actual` y cuyos prerequisitos estén seleccionados.  
- RF4.3: Una vez elegida, `pendiente_eleccion=false` hasta nuevo incremento de nivel.  
- RF4.4: Las ventajas eliminadas o inconsistentes se anulan automáticamente y se marcan como pendientes de nueva selección.

### RF5 — Versionado y Log
- RF5.1: El sistema usa **modelo de snapshots completos** para versionado.  
- RF5.2: Cada cambio genera un snapshot nuevo con la configuración de ventajas elegidas.  
- RF5.3: El máster puede revertir a cualquier snapshot anterior.  
- RF5.4: Todas las acciones se registran en `LogAccion` con fecha, hora, usuario y tipo de evento.

### RF6 — Imágenes
- RF6.1: Las imágenes se almacenan localmente en `/data/images/pj/` y `/data/images/npc/`.  
- RF6.2: Se validan en arranque.  
- RF6.3: Las exportaciones solo guardan la ruta, no la imagen.  
- RF6.4: El máster puede subir nuevas imágenes o reemplazarlas.

### RF7 — Exportación e Importación
- RF7.1: Exportar el estado completo (relaciones, interacciones, snapshots, PJs, NPCs).  
- RF7.2: Importar catálogo o estado desde JSON.  
- RF7.3: Al importar un NPC, reemplaza el existente (por `npc_id`) y recalcula relaciones.  
- RF7.4: Antes de cada importación, se genera automáticamente una copia de seguridad del estado actual.

---

## 5. Requisitos No Funcionales
- RNF1: Simultaneidad: hasta 7 usuarios concurrentes.  
- RNF2: Tiempo de respuesta < 300 ms en operaciones comunes.  
- RNF3: Base de datos H2 en modo archivo (persistente entre ejecuciones).  
- RNF4: Textos fijos en español, formato horario local.  
- RNF5: Longitud de campos de texto libre.  
- RNF6: Los nombres de NPCs y PJs deben ser únicos.  
- RNF7: Layout automático de árboles de ventajas.  
- RNF8: Arquitectura hexagonal (puertos y adaptadores).  
- RNF9: Sin modo oscuro ni traducción multilenguaje.

---

## 6. Modelo de Datos (Resumen)

- `PJ (pj_id, nombre_display, nota_opcional, imagen_url, fecha_creacion)`  
- `NPC (npc_id, nombre, descripcion_larga, nivel_maximo, imagen_url, fecha_importacion)`  
- `VENTAJA (ventaja_id, npc_id, nombre, descripcion_larga, min_nivel_relacion, prerequisitos)`  
- `RELACION (relacion_id, pj_id, npc_id, nivel_actual, pendiente_eleccion, ultima_actualizacion_ts, contador_interacciones, consistente)`  
- `INTERACCION (interaccion_id, relacion_id, tipo, valor, nota, ts, usuario)`  
- `SELECCION_VENTAJA (seleccion_id, relacion_id, nivel_relacion, ventaja_id, ts, usuario)`  
- `SNAPSHOT_SELECCIONES (snapshot_id, relacion_id, version, ts, usuario)`  
- `SNAPSHOT_SELECCION_ITEM (snapshot_id, ventaja_id, nivel_relacion)`  
- `LOG_ACCION (log_id, ts, usuario, accion, detalle)`

---

## 7. Arquitectura Técnica

El sistema se implementará con **arquitectura hexagonal**, separando las capas:
- **Dominio:** entidades, lógica de negocio, validaciones de consistencia.  
- **Aplicación:** casos de uso (servicios) y orquestación.  
- **Infraestructura:** persistencia (Spring Data JPA + H2), import/export JSON, adaptadores REST.  
- **UI (frontend):** React + Vite + TailwindCSS, comunicación vía REST.

### Directorios de datos
```
/data/
  ├─ images/
  │   ├─ pj/
  │   └─ npc/
  ├─ catalogos/
  └─ database.mv.db
```

---

## 8. Criterios de Aceptación (extracto)
- CA01: Subir nivel marca relación como pendiente y genera snapshot.  
- CA02: No se puede elegir una ventaja sin cumplir prerequisitos.  
- CA03: Se validan imágenes al iniciar; faltantes se registran en log.  
- CA04: Importar NPC recalcula consistencias y elimina ventajas rotas.  
- CA05: Revertir snapshot restaura ventajas sin alterar nivel actual.  
- CA06: Exportar estado genera JSON y copia de seguridad previa.  
- CA07: Cada acción relevante se refleja en el `LogAccion` con detalle.

---

## 9. Cierre
Documento aprobado como **versión final de requisitos funcionales y técnicos (v0.7)**.  
Listo para pasar a diseño JPA, definición de endpoints REST y especificación de interfaz UI.

