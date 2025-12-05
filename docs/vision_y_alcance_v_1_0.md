# Documento de Visión y Alcance
### Sistema de Relaciones — Campaña *El Regente de Jade*
**Versión:** 1.0  
**Autor:** Alberto Cebrián  
**Fecha:** Octubre de 2025  

---

## 1. Propósito general

El propósito del sistema de relaciones es ofrecer una herramienta que permita a los jugadores de la campaña *El Regente de Jade* gestionar de forma sencilla y visual sus relaciones con los distintos personajes no jugadores (NPC), así como consultar y seleccionar las ventajas derivadas de dichas relaciones, sin necesidad de recargar sus hojas de personaje físicas o digitales.

Desde la perspectiva del máster, el sistema busca centralizar y simplificar la gestión de estas relaciones, evitando la necesidad de manejar múltiples documentos y notas durante la partida, y permitiendo un control unificado del progreso de cada jugador.

La campaña de Pathfinder *El Regente de Jade* pone un fuerte énfasis en las relaciones con los principales miembros de la caravana —Sandru, Ameiko, Koya y Shalelu—, pero el sistema oficial se considera limitado. Este proyecto amplía dicha mecánica mediante un conjunto de ventajas menores que pueden obtenerse en función del nivel de relación, e incorpora nuevos NPCs para enriquecer las interacciones y favorecer el roleo narrativo dentro del grupo.

Al ofrecer ventajas tangibles, el sistema estimula la participación narrativa de los jugadores, fomentando escenas de interpretación y fortaleciendo el vínculo emocional con los personajes de la campaña.

---

## 2. Usuarios y actores

El sistema está diseñado para un entorno cerrado compuesto por los siguientes usuarios:

- **Máster:** usuario administrador y gestor del sistema. Puede importar NPCs, definir sus árboles de ventajas, gestionar niveles de relación, registrar interacciones y revisar el progreso de los jugadores.
- **Jugadores:** seis usuarios correspondientes a los personajes jugadores (PJs) de la campaña. Pueden consultar sus niveles de relación con los NPCs, ver sus ventajas actuales y seleccionar nuevas ventajas cuando suben de nivel.

La aplicación será utilizada exclusivamente por el máster y los jugadores actuales de la campaña, dentro de la misma red local en la que esté desplegada la aplicación. No se contempla, en esta versión, su uso remoto o multiusuario distribuido.

Aunque la versión inicial no incluye autenticación, se distingue claramente entre los roles de máster y jugador para definir comportamientos y permisos diferenciados dentro del sistema.

---

## 3. Objetivos del sistema

Los objetivos principales del sistema son los siguientes:

1. **Permitir la gestión visual de las relaciones PJ–NPC**, mostrando niveles, ventajas y estados pendientes.  
2. **Automatizar la gestión de niveles de relación y ventajas**, reduciendo la carga manual del máster.  
3. **Proporcionar trazabilidad y coherencia**, manteniendo un registro histórico de interacciones, elecciones y cambios de estado.

Estos objetivos se complementan con la intención narrativa de potenciar el roleo dentro de la campaña y dar relevancia a los vínculos entre personajes.

---

## 4. Alcance funcional

### 4.1 Incluido en la versión inicial (v1.0)
- Creación de PJs y NPCs.  
- Importación de árboles de ventajas desde archivos JSON.  
- Gestión de relaciones PJ–NPC con niveles de progreso.  
- Registro de interacciones positivas y negativas.  
- Sistema de ventajas con prerequisitos y selección por nivel.  
- Almacenamiento y recuperación de estados mediante snapshots.  
- Visualización de relaciones y ventajas desde el navegador.  

### 4.2 Excluido del alcance (Out of scope)
- Sistema de autenticación o control de acceso por usuario.  
- Acceso remoto fuera de la red local.  
- Editor visual de árboles de ventajas.  
- Integraciones con plataformas externas o sistemas de rol virtual.  

---

## 5. Evolutivos previstos

En futuras versiones del sistema se contemplan las siguientes ampliaciones:

1. **Autenticación de usuarios:** diferenciación real de máster y jugadores mediante credenciales y permisos.  
2. **Soporte multijuego o multicampaña:** permitir gestionar varias campañas diferentes, con sus propios conjuntos de PJs, NPCs y árboles de relación.  
3. **Editor visual de árboles de ventajas:** creación y modificación directa desde la interfaz web.  
4. **Exportación e importación de campañas completas:** respaldo y transferencia entre instalaciones.  
5. **Integración con herramientas de rol online (VTT):** sincronización parcial de datos con plataformas de juego.  

---

## 6. Restricciones

- El sistema deberá desarrollarse exclusivamente con **herramientas y librerías gratuitas**.  
- El backend utilizará **Java + Spring Boot** y el frontend se implementará con **React + Tailwind**.  
- Todos los datos se almacenarán localmente en una base **H2** en modo archivo.  
- La aplicación no deberá requerir conexión a Internet para su funcionamiento.  

---

## 7. Criterios de éxito

- Los jugadores deben poder consultar y elegir sus ventajas desde los navegadores de sus dispositivos móviles **sin intervención del máster**.  
- El máster debe poder gestionar, actualizar y revisar las relaciones desde un único panel centralizado.  
- El sistema debe poder desplegarse y usarse íntegramente en un entorno local sin dependencias externas.  

---

**Versión 1.0 — Documento de Visión y Alcance completo.**  
Establece los objetivos, alcance, usuarios, restricciones y criterios de éxito del sistema de relaciones de la campaña *El Regente de Jade*, sirviendo como guía estratégica para el desarrollo y evolución del proyecto.

