# ADR-005: PostgreSQL as Primary Database

## Estado
Aceptado

## Contexto
El sistema requiere una base de datos que:
- garantice consistencia,
- soporte relaciones complejas,
- escale adecuadamente,
- sea robusta en producción.

## Decisión
Se adopta **PostgreSQL 18.3** como base de datos principal.

El acceso se realizará mediante:
- Spring Data JPA
- migraciones controladas con Flyway

## Alternativas consideradas

### MySQL
- Pros: popularidad
- Contras: menor robustez en algunos escenarios

### NoSQL (MongoDB)
- Pros: flexibilidad
- Contras: no adecuado para relaciones complejas

## Consecuencias

### Positivas
- integridad referencial
- soporte de transacciones
- alto rendimiento
- ecosistema maduro

### Negativas
- menor flexibilidad que NoSQL
- requiere diseño adecuado de esquema

## Justificación
PostgreSQL ofrece un balance sólido entre rendimiento, consistencia y escalabilidad para sistemas académicos estructurados.