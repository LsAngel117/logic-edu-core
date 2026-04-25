# ADR-006: Java LTS and Gradle Kotlin DSL

## Estado
Aceptado

## Contexto
Se requiere definir:
- lenguaje base del backend,
- herramienta de construcción,
- compatibilidad a largo plazo.

## Decisión
Se adopta:

- **Java LTS (versión más reciente soportada por el stack, preferiblemente Java 25)**
- **Gradle con Kotlin DSL** como herramienta de build

## Alternativas consideradas

### Maven
- Pros: estándar ampliamente usado
- Contras: menor flexibilidad

### Gradle con Groovy
- Pros: popularidad histórica
- Contras: menor tipado, más propenso a errores

## Consecuencias

### Positivas
- tipado fuerte en scripts (Kotlin DSL)
- mejor soporte en IDE
- flexibilidad en builds
- compatibilidad a largo plazo con Java LTS

### Negativas
- curva de aprendizaje si no se conoce Gradle
- menor adopción que Maven en algunos entornos

## Justificación
Gradle con Kotlin DSL ofrece mayor mantenibilidad y robustez en el tiempo, alineado con prácticas modernas de desarrollo backend.