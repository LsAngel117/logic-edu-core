# Dependency Rules

- domain no depende de ninguna otra capa
- application depende de domain
- infrastructure depende de application y domain
- interfaces depende de application

## Prohibido

- usar JPA en domain
- usar Spring en domain
- lógica en controllers