# API REST — Jade Regent

Esta guía documenta la API expuesta por el backend Spring Boot.

- Base URL local: `http://localhost:8080`
- Prefijo: `/api`
- Formato JSON: `snake_case`

---

## 1. Convenciones

### 1.1 Tipos de identificador

- `pj_id`: UUID.
- `relacion_id`: UUID.
- `npc_id`: string semántico (por ejemplo: `ameiko`).
- `ventaja_id`: string semántico (por ejemplo: `ameiko_cancion_de_animo`).

### 1.2 Códigos esperados

- `200 OK`: operación satisfactoria.
- `201 Created`: creación de recurso.
- `204 No Content`: borrado sin respuesta.
- `400 Bad Request`: request inválida.
- `500 Internal Server Error`: error interno.

> Nota: el manejo de errores aún es básico y no está normalizado con un esquema único de error.

---

## 2. Endpoints de PJ

### 2.1 Listar PJs

`GET /api/pj`

**Respuesta 200**

```json
[
  {
    "pj_id": "d3c364df-e96b-4b57-a643-1e68f8ca6ec1",
    "nombre_display": "Hiriko",
    "nota_opcional": "Maga kitsune",
    "imagen_url": "/data/images/pj/Hiriko.jpg"
  }
]
```

### 2.2 Crear PJ

`POST /api/pj`

**Body**

```json
{
  "nombre_display": "Nuevo PJ",
  "nota_opcional": "Descripción libre",
  "imagen_url": "/data/images/pj/default.jpg"
}
```

**Respuesta 201** (incluye `Location: /api/pj/{id}`)

### 2.3 Obtener detalle de PJ

`GET /api/pj/{id}`

Devuelve el detalle completo del PJ y su estado de relaciones.

### 2.4 Actualizar PJ

`PUT /api/pj/{id}`

Body compatible con `PjDTO`.

### 2.5 Eliminar PJ

`DELETE /api/pj/{id}`

### 2.6 Ventajas imprimibles

`GET /api/pj/{id}/printable-advantages`

Devuelve un DTO orientado a formato de impresión/resumen.

---

## 3. Endpoints de NPC

### 3.1 Listar NPCs

`GET /api/npc`

### 3.2 Obtener detalle de NPC

`GET /api/npc/{id}`

### 3.3 Actualizar NPC

`PUT /api/npc/{id}`

**Body**

```json
{
  "nombre": "Ameiko Kaijitsu",
  "descripcion_larga": "Texto narrativo",
  "imagen_url": "/data/images/npc/ameiko.jpg"
}
```

### 3.4 Eliminar NPC

`DELETE /api/npc/{id}`

### 3.5 Importar NPC desde JSON

`POST /api/npc/import`

- Content-Type: `multipart/form-data`
- Campo: `file`

Ejemplo con `curl`:

```bash
curl -X POST http://localhost:8080/api/npc/import \
  -F "file=@data/catalogos/ameiko.json"
```

---

## 4. Endpoints de relaciones

### 4.1 Crear relación PJ/NPC

`POST /api/relaciones`

**Body**

```json
{
  "pj_id": "d3c364df-e96b-4b57-a643-1e68f8ca6ec1",
  "npc_id": "ameiko"
}
```

### 4.2 Registrar interacción

`POST /api/relaciones/{relacionId}/interacciones`

**Body**

```json
{
  "tipo": "POSITIVA",
  "nota": "Compartió información clave"
}
```

Tipos soportados: `POSITIVA`, `NEGATIVA`.

### 4.3 Subir o bajar nivel

`PATCH /api/relaciones/{relacionId}/nivel?increment=true`

- `increment=true` → subir nivel.
- `increment=false` → bajar nivel.

### 4.4 Seleccionar ventaja

`POST /api/relaciones/{relacionId}/ventajas/{ventajaId}`

Marca una ventaja como elegida para la relación en su nivel de adquisición.

### 4.5 Eliminar relación

`DELETE /api/relaciones/{relacionId}`

### 4.6 Matriz global

`GET /api/relaciones/matrix`

Retorna estructura matricial para visualizar estado de todas las combinaciones PJ-NPC.

---

## 5. Endpoints de backup

### 5.1 Exportar backup lógico

`GET /api/backup`

Devuelve un JSON con el estado completo necesario para restaurar datos de la aplicación.

### 5.2 Restaurar backup lógico

`POST /api/backup/restore`

**Body**: objeto `FullBackupDTO` previamente exportado.

---

## 6. Ejemplos de flujo completo

1. Crear PJ (`POST /api/pj`).
2. Importar NPC (`POST /api/npc/import`).
3. Crear relación (`POST /api/relaciones`).
4. Registrar interacción positiva (`POST /api/relaciones/{id}/interacciones`).
5. Subir nivel (`PATCH /api/relaciones/{id}/nivel?increment=true`).
6. Seleccionar ventaja (`POST /api/relaciones/{id}/ventajas/{ventajaId}`).
7. Descargar backup (`GET /api/backup`).

---

## 7. Consideraciones para clientes frontend

- El frontend actual mezcla `fetch` y `axios`.
- Se recomienda centralizar cliente HTTP para:
  - interceptores,
  - trazabilidad,
  - gestión homogénea de errores,
  - y retries opcionales.
- Si se separa frontend/backend por dominio, configurar CORS en backend y `baseURL` explícita en frontend.

