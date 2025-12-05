# Documento de Despliegue y Configuración (v1.2)
### Sistema de Relaciones — Campaña *El Regente de Jade*
**Versión:** 1.2  
**Autor:** Alberto Cebrián  
**Fecha:** Octubre de 2025  

---

## 1. Objetivo
Esta versión actualiza el método de despliegue del *Sistema de Relaciones* para incluir la instalación de Node.js y npm, integrar completamente el **frontend (React + Tailwind)** dentro del **backend (Spring Boot)** y simplificar el acceso en red local.  
El sistema se ejecuta desde un único archivo `.jar`, accesible en `http://192.168.1.201:8080` para todos los dispositivos de la red.

---

## 2. Entorno de despliegue

### 2.1 Requisitos mínimos
| Componente | Requisito |
|-------------|------------|
| Sistema operativo | Windows 11 Pro |
| Procesador | Dual-Core 2.0 GHz o superior |
| Memoria RAM | 2 GB mínimo |
| Espacio en disco | 200 MB libres |
| JDK | Java 23 |
| Maven | Versión 3.9 o superior |
| Node.js | Versión LTS (18 o superior) |
| Navegador | Chrome / Firefox / Edge (última versión) |

### 2.2 Estructura recomendada de carpetas
```
\jade_relations\
 ├── backend\
 │    ├── jade-relations.jar
 │    ├── application.properties
 │    ├── data\ (base de datos H2)
 │    ├── logs\
 │    └── import\ (archivos JSON)
 └── frontend\
      ├── src\
      ├── public\
      └── build\ (generado tras compilación)
```
Las rutas deben mantenerse **relativas** para facilitar el traslado del sistema entre equipos.

---

## 3. Instalación del entorno

### 3.1 Instalar Java 23 (JDK)
1. Acceder a [https://jdk.java.net/23/](https://jdk.java.net/23/)
2. Descargar el instalador de Windows (x64).
3. Ejecutar con las opciones por defecto.
4. Verificar instalación:
   ```bash
   java -version
   ```
   Resultado esperado:
   ```
   openjdk version "23" 2025-xx-xx
   ```
5. Configurar `JAVA_HOME` si fuera necesario:
   ```bash
   setx JAVA_HOME "C:\Program Files\Java\jdk-23"
   ```

### 3.2 Instalar Apache Maven
1. Descargar Maven desde [https://maven.apache.org/download.cgi](https://maven.apache.org/download.cgi)
2. Extraer en `C:\Program Files\Maven`.
3. Añadir al `PATH`:
   ```bash
   setx PATH "%PATH%;C:\Program Files\Maven\bin"
   ```
4. Verificar instalación:
   ```bash
   mvn -version
   ```

### 3.3 Instalar Node.js y npm
1. Acceder a [https://nodejs.org/en/download/](https://nodejs.org/en/download/)
2. Descargar el instalador **LTS** para Windows.
3. Ejecutar el instalador con las opciones por defecto.
4. Verificar instalación:
   ```bash
   node -v
   npm -v
   ```
   Ambos comandos deben devolver sus respectivas versiones.

---

## 4. Compilación e integración del proyecto

### 4.1 Compilar el frontend (React + Tailwind)
1. Abrir una terminal en `\jade_relations\frontend`.
2. Instalar dependencias:
   ```bash
   npm install
   ```
3. Generar la compilación del frontend:
   ```bash
   npm run build
   ```
   Esto creará el directorio `frontend/build/`.

### 4.2 Integrar el frontend en el backend
1. Abrir una terminal dentro de `frontend/`.
2. Limpiar el contenido anterior del directorio `static/` (si existe):
   ```bash
   rimraf ..\backend\src\main\resources\static\*
   ```
3. Copiar el nuevo *build* dentro del backend:
   ```bash
   xcopy build ..\backend\src\main\resources\static /E /I /Y
   ```

### 4.3 Empaquetar todo en un único .jar
1. Abrir una terminal en `backend/`.
2. Ejecutar Maven para compilar y empaquetar:
   ```bash
   mvn clean package
   ```
3. El archivo final `jade-relations.jar` se generará en `backend/target/`.
4. Copiarlo a la carpeta principal `\jade_relations\backend\`.

### 4.4 Ejecutar la aplicación integrada
1. En la carpeta `backend/`, iniciar el sistema:
   ```bash
   java -jar jade-relations.jar
   ```
2. Esperar el mensaje:
   ```
   Started JadeRelationsApplication in X seconds
   ```
3. Abrir un navegador y acceder a:
   ```
   http://localhost:8080
   ```
   o desde otro dispositivo de la red local:
   ```
   http://192.168.1.201:8080
   ```

---

## 5. Configuración adicional

### 5.1 Carpeta de imágenes
Las imágenes de PJ y NPC deben almacenarse en `frontend/public/images/`.  
Durante la compilación, se copian automáticamente al *build* y se sirven junto con los demás archivos estáticos.  
Para actualizarlas, reemplazar las imágenes y volver a ejecutar `npm run build`.

### 5.2 Configuración de CORS
Al integrarse todo en el mismo origen (`localhost:8080`), **no es necesario configurar CORS**.  
Solo será necesario si se usa el modo de desarrollo separado (ver nota final).

---

## 6. Acceso desde otros dispositivos

### 6.1 Acceso local
En el PC del máster, abrir un navegador y acceder a:
```
http://localhost:8080
```

### 6.2 Acceso desde dispositivos en la misma red
1. En el PC del máster, ejecutar:
   ```bash
   ipconfig
   ```
2. Localizar la dirección IPv4 (`192.168.1.201`).
3. En el móvil o tablet conectado a la misma red, acceder a:
   ```
   http://192.168.1.201:8080
   ```

### 6.3 Problemas de conexión
- Verificar que el firewall de Windows permite el puerto **8080**.  
- Comprobar que todos los dispositivos están en la **misma subred**.  
- Si se usa antivirus con firewall, crear una excepción.  
- Desactivar temporalmente el firewall solo para pruebas, si fuera necesario.

---

## 7. Mantenimiento del sistema

### 7.1 Copias de seguridad (manuales y automáticas)
#### Manuales
1. Detener el backend si está en ejecución.
2. Copiar el archivo `backend/data/database.mv.db` a una carpeta segura (`\jade_relations\backups\`).

#### Automáticas
1. Abrir el **Programador de tareas** de Windows.  
2. Crear una nueva tarea con el comando:
   ```bash
   xcopy .\jade_relations\backend\data\database.mv.db .\jade_relations\backups\ /Y
   ```
3. Programarla para ejecución diaria o semanal.

### 7.2 Restaurar una copia de seguridad
1. Detener la aplicación (`Ctrl + C` en la terminal).  
2. Sustituir el archivo `database.mv.db` actual por la copia deseada.  
3. Reiniciar el sistema.

### 7.3 Reinicializar la base de datos
1. Detener la aplicación.  
2. Eliminar todos los archivos dentro de `backend/data/`.  
3. Al reiniciar, el sistema generará una base de datos vacía automáticamente.

---

## 8. Nota para desarrolladores — Modo separado
Durante el desarrollo activo del frontend, puede ser útil usar React con recarga automática (*hot reload*).  
Para ello:
1. En `frontend/`, ejecutar:
   ```bash
   npm start
   ```
   Esto levantará el frontend en `http://localhost:3000`.
2. Mantener el backend en ejecución en `http://localhost:8080`.
3. Activar CORS en `application.properties` para permitir el acceso cruzado:
   ```bash
   spring.web.cors.allowed-origins=http://localhost:3000
   ```
4. Al finalizar, volver a empaquetar el frontend según los pasos del apartado **4. Compilación e integración del proyecto**.

---

## 9. Conclusión
Esta versión 1.2 actualiza el proceso de instalación y despliegue, añadiendo Node.js y simplificando el flujo completo de compilación, empaquetado y ejecución.  
El sistema ahora puede ejecutarse con un único comando y es accesible desde cualquier dispositivo conectado a `192.168.1.201:8080`.

**Versión 1.2 — Documento de Despliegue y Configuración completo y actualizado.**

