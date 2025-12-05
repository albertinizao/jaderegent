# Documento de Mantenimiento y Evolución (v1.0)
### Sistema de Relaciones — Campaña *El Regente de Jade*
**Versión:** 1.0  
**Autor:** Alberto Cebrián  
**Fecha:** Octubre de 2025  

---

## 1. Objetivo
Este documento define las pautas de **mantenimiento técnico** y **evolución futura** del *Sistema de Relaciones* de la campaña *El Regente de Jade*.  
Su propósito es asegurar la estabilidad, seguridad y capacidad de crecimiento del sistema a lo largo del tiempo, garantizando que pueda mantenerse fácilmente antes de cada sesión de rol y evolucionar sin comprometer los datos existentes.

---

## 2. Alcance
El mantenimiento descrito aplica al entorno local del máster, donde se ejecuta el sistema en un PC con Windows 11.  
Las recomendaciones incluyen tanto acciones preventivas (copias, revisión de logs, actualización de dependencias) como evolutivas (mejoras funcionales, migraciones, y ampliaciones futuras).

---

## 3. Checklist de mantenimiento previo a cada sesión
Antes de iniciar cada sesión de rol, se recomienda realizar las siguientes tareas:

1. **Crear una copia de seguridad manual** de la base de datos:
   ```bash
   xcopy .\jade_relations\backend\data\database.mv.db .\jade_relations\backups\ /Y
   ```
2. **Verificar los logs del sistema** (`backend/logs/app.log`) en busca de errores o avisos repetidos.
3. **Comprobar la conexión de red** y confirmar que el puerto `8080` no esté en uso.
4. **Actualizar dependencias** (si han pasado más de 30 días):
   ```bash
   mvn versions:display-dependency-updates
   npm outdated
   ```
5. **Reconstruir el proyecto** si hubo cambios recientes en frontend o backend:
   ```bash
   npm run build
   mvn clean package
   ```
6. **Probar el arranque local** para asegurar que el sistema inicia correctamente:
   ```bash
   java -jar jade-relations.jar
   ```
7. Confirmar que todos los jugadores pueden acceder a la IP del máster (`http://192.168.1.201:8080`).

---

## 4. Mantenimiento preventivo

### 4.1 Frecuencia recomendada
- **Antes de cada sesión de rol:** ejecutar la checklist anterior.
- **Mensualmente:** revisar logs, limpiar archivos antiguos, y actualizar dependencias menores.
- **Trimestralmente:** comprobar actualizaciones de JDK, Node.js y librerías principales (React, Spring Boot).

### 4.2 Limpieza del sistema
Para mantener la estabilidad del entorno:
- Eliminar compilaciones antiguas de `frontend/build/` antes de generar una nueva.
- Revisar el tamaño de `backend/logs/` y eliminar archivos mayores de 10 MB.
- Vaciar `backend/data/` solo cuando se reinicie completamente la campaña.

### 4.3 Comprobación de rendimiento
1. Observar los tiempos de carga de la aplicación.
2. Verificar que las consultas y renderizados responden en menos de 2 segundos.
3. Si el rendimiento empeora, revisar el tamaño de la base de datos H2 y considerar su compactación.

---

## 5. Estrategia de versionado

### 5.1 Convención de versiones
Se empleará un **versionado semántico** en formato `vX.Y.Z`:
- **X** → Cambios mayores o incompatibles (reestructuración de entidades, migración de datos).
- **Y** → Nuevas funcionalidades o mejoras no disruptivas.
- **Z** → Correcciones de errores o ajustes menores.

Ejemplo:  
- `v1.0.0`: Primera versión estable.  
- `v1.1.0`: Añadida autenticación básica.  
- `v1.1.2`: Corregido error en importación de NPC.

### 5.2 Control de versiones y documentación
- Mantener los documentos (SRS, SAD, Despliegue, etc.) versionados de forma paralela al código.
- Incluir el número de versión en el encabezado de cada documento.
- Actualizar el registro de cambios (`CHANGELOG.md`) tras cada actualización significativa.

---

## 6. Migraciones y compatibilidad

### 6.1 Migración de base de datos
Si se cambia de H2 a otro sistema (por ejemplo, PostgreSQL):
1. Exportar la base actual:
   ```bash
   SCRIPT TO 'export.sql'
   ```
2. Crear la nueva base y ejecutar el script exportado.
3. Actualizar la configuración en `application.properties`:
   ```bash
   spring.datasource.url=jdbc:postgresql://localhost:5432/jade_relations
   spring.datasource.username=usuario
   spring.datasource.password=clave
   ```

### 6.2 Compatibilidad hacia atrás
- Mantener los nombres de campos y endpoints REST siempre que sea posible.
- Si se eliminan o renuevan atributos, ofrecer un conversor automático o un script de ajuste de datos.
- Las versiones menores deben ser siempre compatibles con las anteriores.

---

## 7. Actualización del entorno de desarrollo

### 7.1 Dependencias críticas
- **Java:** comprobar actualizaciones LTS anualmente.
- **Spring Boot:** revisar cada 6 meses.
- **React y Tailwind:** mantener dentro de versiones LTS estables.
- **npm y Maven:** actualizar solo tras probar la compatibilidad del proyecto.

### 7.2 Procedimiento recomendado
1. Crear copia de seguridad del proyecto completo.  
2. Ejecutar `npm audit fix` y `mvn dependency:resolve` para identificar posibles conflictos.  
3. Probar el sistema localmente antes de distribuir una nueva versión.  
4. Verificar los logs tras la primera ejecución con las nuevas dependencias.

---

## 8. Evolución funcional prevista

### 8.1 Mejoras a corto plazo
- Implementar **autenticación básica** para diferenciar jugadores y máster.  
- Añadir **pantalla de estadísticas**: total de interacciones positivas/negativas, niveles medios de relación.  
- Permitir **exportar datos** a PDF o CSV (relaciones, ventajas, interacciones).  
- Añadir **modo presentación** para mostrar gráficos del progreso de relaciones.

### 8.2 Mejoras a medio plazo
- Soporte para **múltiples campañas** con su propio conjunto de PJ y NPC.  
- Editor visual del **árbol de ventajas** (drag & drop).  
- Sistema de **notificaciones internas** cuando un PJ pueda elegir nueva ventaja.  
- Incorporación de **sistema de etiquetado** de NPC (por ejemplo: Caravana, Enemigo, Aliado).

### 8.3 Mejoras a largo plazo
- Migración de H2 a una base de datos relacional completa (PostgreSQL).  
- Integración con servicios de almacenamiento en la nube para backups automáticos.  
- API pública para exportar información a otras herramientas o apps complementarias.  
- Rediseño de la interfaz para dispositivos táctiles (modo tablet optimizado).

---

## 9. Recomendaciones finales
- Realizar el mantenimiento **antes de cada sesión de rol**, siguiendo la checklist del apartado 3.  
- Documentar los cambios y mantener sincronizadas las versiones de código y documentos.  
- Probar cada nueva versión del sistema en local antes de usarla en una sesión real.  
- Priorizar la estabilidad sobre las nuevas funcionalidades.

---

## 10. Conclusión
El presente documento establece un marco claro para el mantenimiento y evolución del *Sistema de Relaciones — El Regente de Jade*.  
Siguiendo estas pautas, el sistema podrá mantenerse operativo, seguro y preparado para futuras ampliaciones sin comprometer la jugabilidad ni la integridad de los datos.

**Versión 1.0 — Documento de Mantenimiento y Evolución completo.**

