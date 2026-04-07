# Guía Operativa y de Mantenimiento

Esta guía está orientada al uso diario del sistema en entorno de desarrollo o despliegue local de mesa de juego.

---

## 1. Arranque rápido

### 1.1 Backend

```bash
./mvnw spring-boot:run
```

### 1.2 Frontend

```bash
cd frontend
npm install
npm run dev
```

### 1.3 URLs típicas

- App frontend (Vite): `http://localhost:5173`
- API backend: `http://localhost:8080/api`
- Consola H2: `http://localhost:8080/h2-console`

Parámetros H2 por defecto:

- JDBC URL: `jdbc:h2:file:./data/database`
- User: `sa`
- Password: `password`

---

## 2. Operación funcional recomendada

### 2.1 Antes de sesión

1. Verificar que backend y frontend arrancan sin error.
2. Realizar backup (`GET /api/backup`) y guardar con fecha.
3. Validar que assets de imágenes existen en `data/images/...`.

### 2.2 Durante sesión

1. Crear nuevos PJ si hace falta.
2. Registrar interacciones a medida que avanza la historia.
3. Actualizar nivel cuando corresponda por reglas de campaña.
4. Resolver elecciones de ventajas pendientes.

### 2.3 Después de sesión

1. Descargar backup final.
2. Archivar backup con convención: `backup_jaderegent_YYYY-MM-DD.json`.
3. (Opcional) exportar capturas o listados para acta de sesión.

---

## 3. Backups y restauración

### 3.1 Exportación

- Opción UI: botón de backup en dashboard (modo máster).
- Opción API: `GET /api/backup`.

### 3.2 Restauración

- Opción UI: botón restaurar en dashboard (modo máster).
- Opción API: `POST /api/backup/restore` con JSON del backup.

### 3.3 Buenas prácticas

- Mantener múltiples puntos de recuperación (al menos 3 últimas sesiones).
- No sobreescribir el backup anterior; generar siempre archivo nuevo.
- Validar restauración en entorno de pruebas antes de usar en producción de campaña.

---

## 4. Importación de NPCs

### 4.1 Requisitos del JSON

- Debe representar un NPC válido para el modelo del sistema.
- Idealmente incluir ventajas y metadatos coherentes (`nivel_maximo`, prerequisitos, etc.).

### 4.2 Procedimiento

1. Preparar archivo JSON.
2. Importar por UI (`/import-npc`) o endpoint REST.
3. Verificar en listado de NPCs y detalle.
4. Comprobar que árbol de ventajas y relaciones existentes siguen consistentes.

---

## 5. Solución de problemas

### 5.1 El frontend no conecta con API

- Verificar backend en puerto `8080`.
- Revisar proxy de Vite en `frontend/vite.config.js`.
- Confirmar que no haya otro proceso usando `8080`.

### 5.2 Error en carga de imágenes

- Confirmar archivo físico bajo `data/images/...`.
- Revisar que URL guardada use prefijo `/data/...`.
- Verificar mapeo de recursos estáticos (`/data/**`).

### 5.3 Fallo al restaurar backup

- Validar que el JSON sea exportado por esta misma app.
- Revisar estructura y tipos de IDs.
- Consultar logs del backend para identificar entidad conflictiva.

### 5.4 H2 bloqueada o inconsistente

- Detener la app.
- Copiar `data/` a una carpeta segura.
- Restaurar desde backup lógico si procede.

---

## 6. Mantenimiento técnico

### 6.1 Dependencias backend

```bash
./mvnw -q dependency:tree
```

### 6.2 Verificación backend

```bash
./mvnw test
```

### 6.3 Verificación frontend

```bash
cd frontend
npm run lint
npm run build
```

### 6.4 Limpieza de build

```bash
./mvnw clean
```

---

## 7. Checklist de release interno

- [ ] `mvn test` en verde.
- [ ] `npm run lint` y `npm run build` en verde.
- [ ] Backup de datos actual generado y archivado.
- [ ] Revisión manual de vistas críticas (dashboard, PJ detail, matriz).
- [ ] Documentación actualizada (`README.md`, `docs/API.md`, `docs/GUIA_OPERATIVA.md`).

