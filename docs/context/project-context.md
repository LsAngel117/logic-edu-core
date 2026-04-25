# Contexto del Proyecto — Logic Edu Core Platform

## 1. Resumen general

**Logic Edu Core Platform** es una plataforma web y móvil para la gestión académica de instituciones educativas, inicialmente enfocada en escuelas de teología, pero diseñada desde el inicio para poder escalar a otras escuelas, seminarios o iglesias que requieran control de asistencia, seguimiento de progreso y administración académica.

El sistema reemplaza procesos manuales basados en planillas físicas o archivos dispersos por una solución centralizada, trazable y segura.

---

## 2. Problema que resuelve

Actualmente, el registro de asistencia y progreso académico se realiza de forma manual, lo que genera:

- pérdida o inconsistencias en la información,
- dificultad para hacer seguimiento histórico,
- baja trazabilidad por estudiante,
- más carga operativa para administradores y maestros,
- poca escalabilidad si la institución crece o se replica en otras sedes.

La plataforma busca digitalizar y profesionalizar este flujo, permitiendo registrar, consultar y gestionar la información académica desde una única fuente de verdad.

---

## 3. Objetivo principal

Construir una solución robusta, escalable y mantenible que permita:

- administrar usuarios y roles,
- organizar la estructura académica,
- registrar asistencia de estudiantes,
- llevar el progreso académico por estudiante,
- generar notificaciones y alertas,
- soportar múltiples instituciones o escuelas en el futuro.

---

## 4. Usuarios del sistema

### Administrador
Responsable de la configuración general del sistema, administración de usuarios, estructura académica, permisos y supervisión general.

### Maestro
Responsable de registrar asistencia, avances y observaciones sobre los estudiantes de sus grupos o materias.

### Estudiante
Usuario que consulta su propia información académica, asistencia, progreso y notificaciones permitidas según permisos.

---

## 5. Alcance funcional inicial

El sistema debe contemplar, como base:

- autenticación y autorización de usuarios,
- gestión de usuarios y roles,
- administración de instituciones,
- ciclos, semestres, cursos, grupos y horarios,
- matrícula o asignación de estudiantes,
- registro de asistencia por sesión,
- registro de progreso académico,
- notificaciones y alertas,
- consultas y reportes básicos.

---

## 6. Consideraciones de crecimiento

La solución no debe diseñarse como una aplicación cerrada para una sola escuela. Debe contemplarse desde el inicio como una plataforma con posibilidad de crecer a:

- varias instituciones,
- diferentes sedes,
- nuevos programas académicos,
- más tipos de usuarios o reglas,
- integración futura con nuevas aplicaciones cliente.

Por eso se adopta una arquitectura que favorezca el desacoplamiento y la escalabilidad.

---

## 7. Arquitectura seleccionada

El proyecto seguirá principios de **Arquitectura Hexagonal (Ports and Adapters)** y **Clean Architecture**, con separación clara entre responsabilidades.

### Capas principales

#### Dominio
Contiene las reglas de negocio puras, entidades, value objects y lógica central del sistema. No depende de frameworks ni de detalles técnicos.

#### Aplicación
Contiene los casos de uso del sistema, coordinando la ejecución de reglas de negocio y la comunicación con puertos definidos por interfaces.

#### Infraestructura
Contiene las implementaciones concretas de persistencia, seguridad, integraciones externas, mensajería y demás dependencias técnicas.

#### Interfaces
Contiene los controladores REST y los puntos de entrada hacia el sistema, tanto para web como para app móvil.

---

## 8. Principios de diseño

El desarrollo debe seguir buenas prácticas profesionales y criterios de nivel senior:

- separación estricta entre dominio y frameworks,
- dependencias dirigidas hacia el núcleo de negocio,
- uso de interfaces para desacoplar infraestructura,
- diseño orientado a casos de uso y no solo a tablas,
- validación de reglas de negocio en el backend,
- código legible, mantenible y testeable,
- control explícito de seguridad y autorización,
- preparación para evolución sin reescritura completa.

---

## 9. Stack tecnológico seleccionado

### Backend
- Java 21+ o la versión LTS más reciente soportada por el stack
- Spring Boot
- Gradle con Kotlin DSL
- REST API

### Persistencia
- PostgreSQL
- Spring Data JPA como capa de acceso a datos
- Flyway para migraciones versionadas

### Seguridad
- Spring Security
- autenticación basada en tokens
- control de acceso por roles

### Frontend web
- Angular

### App móvil
- Kotlin para Android

### Formato de intercambio
- JSON

---

## 10. Decisiones técnicas importantes

### Packaging
Se prefiere generar el backend como **JAR ejecutable**, ya que simplifica el despliegue y encaja mejor con Spring Boot moderno.

### Configuración
Se prefiere **YAML** para configuración del proyecto por su legibilidad y estructura jerárquica.

### Lombok
No se considera imprescindible. Puede omitirse si el equipo prefiere mantener el código explícito y reducir dependencia de generación automática.

### Base de datos
Se recomienda una estrategia con diseño controlado por el dominio, entidades persistentes separadas del modelo de negocio y migraciones explícitas. No se debe dejar la estructura de datos al azar ni depender completamente de generación automática.

---

## 11. Comunicación entre clientes y backend

La web Angular y la app Kotlin consumirán el mismo backend mediante una API REST.

Esto garantiza:

- una sola lógica de negocio central,
- consistencia de datos,
- menor duplicación,
- mantenimiento más simple,
- posibilidad de agregar nuevos clientes en el futuro.

---

## 12. Enfoque de escalabilidad

La escalabilidad se abordará desde dos niveles:

### Escalabilidad funcional
Capacidad de agregar nuevos módulos, nuevas reglas académicas y nuevas instituciones.

### Escalabilidad técnica
Capacidad de soportar crecimiento de usuarios, volumen de datos y nuevas integraciones sin romper la arquitectura base.

Se recomienda iniciar como un **monolito modular bien estructurado**, en lugar de microservicios, para evitar complejidad innecesaria en una fase temprana.

---

## 13. Visión a futuro

A mediano plazo, la plataforma puede evolucionar hacia:

- gestión académica multi-institución,
- reportes avanzados,
- notificaciones automáticas,
- analítica de progreso,
- integración con nuevas apps móviles,
- mayor automatización administrativa,
- potencial modelo SaaS.

---

## 14. Estado conceptual del proyecto

El proyecto se encuentra en fase de definición arquitectónica y diseño del núcleo de negocio. En esta etapa es importante priorizar:

- claridad del dominio,
- definición de casos de uso,
- diseño de entidades y reglas,
- estructura de paquetes,
- contratos entre capas,
- estrategia de persistencia y seguridad.

---

## 15. Propósito del documento

Este contexto sirve como base de referencia para:

- diseñar la arquitectura del proyecto,
- tomar decisiones técnicas consistentes,
- guiar el desarrollo de forma profesional,
- mantener alineado el trabajo futuro con la visión del sistema.

