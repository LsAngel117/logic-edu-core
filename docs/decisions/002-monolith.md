# ADR-002: Monolithic Modular Architecture

## Estado
Aceptado

## Contexto
El sistema se encuentra en una fase inicial de desarrollo y requiere una arquitectura que permita evolucionar sin introducir complejidad innecesaria.

Se evaluó la posibilidad de iniciar con microservicios, pero esto implica:
- mayor complejidad operativa,
- despliegue distribuido,
- gestión de comunicación entre servicios,
- sobrecarga en desarrollo inicial.

## Decisión
Se adopta una arquitectura de **monolito modular por subdominios + capas**.

El sistema se construirá como una única aplicación desplegable, organizada internamente en módulos con subdominios bien definidos (user, school, branch, academic, etc.), respetando separación de responsabilidades.

## Alternativas consideradas

### Microservicios
- Pros: escalabilidad independiente, despliegue distribuido
- Contras: complejidad alta, innecesaria en fase inicial

### Monolito tradicional (sin modularización)
- Pros: simplicidad
- Contras: acoplamiento alto, difícil mantenimiento

## Consecuencias

### Positivas
- menor complejidad inicial
- despliegue simple
- facilidad de desarrollo
- coherencia transaccional

### Negativas
- menor escalabilidad independiente por módulo
- posible necesidad de refactor futuro si el sistema crece significativamente

## Justificación
El monolito modular permite un balance adecuado entre simplicidad y mantenibilidad, alineado con el tamaño y etapa actual del proyecto.
