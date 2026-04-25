# ADR-003: REST API for Client Communication

## Estado
Aceptado

## Contexto
El sistema contará con múltiples clientes:
- aplicación web (Angular)
- aplicación móvil (Kotlin)

Se requiere un mecanismo de comunicación estándar, interoperable y fácil de consumir.

## Decisión
Se adopta una API basada en **REST sobre HTTP**, utilizando JSON como formato de intercambio.

## Alternativas consideradas

### GraphQL
- Pros: flexibilidad en consultas
- Contras: mayor complejidad, innecesario en esta fase

### gRPC
- Pros: alto rendimiento
- Contras: mayor complejidad, menos amigable para frontend

## Consecuencias

### Positivas
- estándar ampliamente adoptado
- fácil integración con frontend y mobile
- facilidad de debugging
- herramientas maduras (OpenAPI, Swagger)

### Negativas
- posible sobrecarga en payloads en algunos casos
- menor flexibilidad que GraphQL

## Justificación
REST ofrece el mejor equilibrio entre simplicidad, compatibilidad y mantenimiento para el contexto del proyecto.