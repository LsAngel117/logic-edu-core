# Error Handling Strategy - LogiEdu Core

## Objetivo

Estandarizar el manejo de errores en Backend y Frontend utilizando códigos de error estables (`ErrorCode`) en lugar de depender de mensajes de texto.

Esta estrategia garantiza:

* Escalabilidad para nuevos módulos.
* Internacionalización futura (i18n).
* Consistencia entre Backend, Frontend y OpenAPI.
* Menor acoplamiento entre capas.
* Arquitectura Hexagonal alineada con Domain-Driven Design.

---

# Regla Arquitectónica Principal

Los Use Cases NO deben lanzar:

```java
IllegalArgumentException
IllegalStateException
NoSuchElementException
```

para representar errores de negocio.

Estas excepciones son genéricas y no expresan correctamente la intención del dominio.

En su lugar se utilizarán excepciones propias del dominio que contengan un `ErrorCode`.

---

# Estructura

```text
shared/
└── errors/
    ├── ErrorCode.java
    ├── ErrorResponse.java
    └── exceptions/
        ├── AuthenticationException.java
        ├── ResourceNotFoundException.java
        ├── BusinessRuleException.java
        └── AccessDeniedException.java
```

---

# ErrorCode

Archivo:

```text
shared/errors/ErrorCode.java
```

Ejemplo inicial:

```java
public enum ErrorCode {

    // Auth
    AUTH_INVALID_CREDENTIALS,
    AUTH_ACCOUNT_DISABLED,
    AUTH_ACCOUNT_LOCKED,
    AUTH_TOKEN_EXPIRED,
    AUTH_TOKEN_INVALID,
    AUTH_ACCESS_DENIED,

    // Users
    USER_NOT_FOUND,
    USER_ALREADY_EXISTS,
    USER_INACTIVE,

    // Schools
    SCHOOL_NOT_FOUND,
    SCHOOL_ALREADY_EXISTS,

    // Branches
    BRANCH_NOT_FOUND,
    BRANCH_ALREADY_EXISTS,

    // Memberships
    MEMBERSHIP_NOT_FOUND,
    MEMBERSHIP_ALREADY_EXISTS,

    // Generic
    VALIDATION_ERROR,
    BUSINESS_RULE_VIOLATION,
    INTERNAL_SERVER_ERROR
}
```

---

# Excepciones de Dominio

## AuthenticationException

```java
public class AuthenticationException extends RuntimeException {

    private final ErrorCode code;

    public AuthenticationException(
            ErrorCode code,
            String message
    ) {
        super(message);
        this.code = code;
    }

    public ErrorCode getCode() {
        return code;
    }
}
```

---

## ResourceNotFoundException

```java
public class ResourceNotFoundException extends RuntimeException {

    private final ErrorCode code;

    public ResourceNotFoundException(
            ErrorCode code,
            String message
    ) {
        super(message);
        this.code = code;
    }

    public ErrorCode getCode() {
        return code;
    }
}
```

---

## BusinessRuleException

```java
public class BusinessRuleException extends RuntimeException {

    private final ErrorCode code;

    public BusinessRuleException(
            ErrorCode code,
            String message
    ) {
        super(message);
        this.code = code;
    }

    public ErrorCode getCode() {
        return code;
    }
}
```

---

# Uso en Use Cases

## Incorrecto

```java
if (!matches) {
    throw new IllegalArgumentException(
            "Invalid credentials"
    );
}
```

---

## Correcto

```java
if (!matches) {
    throw new AuthenticationException(
            ErrorCode.AUTH_INVALID_CREDENTIALS,
            "Invalid credentials"
    );
}
```

---

## Incorrecto

```java
.orElseThrow(() ->
        new IllegalArgumentException(
                "User not found"
        ));
```

---

## Correcto

```java
.orElseThrow(() ->
        new ResourceNotFoundException(
                ErrorCode.USER_NOT_FOUND,
                "User not found"
        ));
```

---

# GlobalExceptionHandler

Ubicación:

```text
interfaces/rest/advice/GlobalExceptionHandler.java
```

Debe mapear excepciones especializadas.

Ejemplo:

```java
@ExceptionHandler(AuthenticationException.class)
public ResponseEntity<ErrorResponse> handleAuthentication(
        AuthenticationException ex,
        HttpServletRequest request
) {

    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(ErrorResponse.of(
                    401,
                    "Unauthorized",
                    ex.getCode().name(),
                    ex.getMessage(),
                    request.getRequestURI()
            ));
}
```

---

```java
@ExceptionHandler(ResourceNotFoundException.class)
public ResponseEntity<ErrorResponse> handleNotFound(
        ResourceNotFoundException ex,
        HttpServletRequest request
) {

    return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ErrorResponse.of(
                    404,
                    "Not Found",
                    ex.getCode().name(),
                    ex.getMessage(),
                    request.getRequestURI()
            ));
}
```

---

```java
@ExceptionHandler(BusinessRuleException.class)
public ResponseEntity<ErrorResponse> handleBusinessRule(
        BusinessRuleException ex,
        HttpServletRequest request
) {

    return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
            .body(ErrorResponse.of(
                    422,
                    "Unprocessable Entity",
                    ex.getCode().name(),
                    ex.getMessage(),
                    request.getRequestURI()
            ));
}
```

---

# ErrorResponse

Formato estándar para toda la API.

```json
{
  "timestamp": "2026-06-04T19:07:42Z",
  "status": 401,
  "error": "Unauthorized",
  "code": "AUTH_INVALID_CREDENTIALS",
  "message": "Invalid credentials",
  "path": "/auth/login"
}
```

---

# Frontend Angular

El frontend NO debe depender del campo:

```json
message
```

porque:

* puede cambiar.
* puede traducirse.
* puede personalizarse.

Debe depender únicamente de:

```json
code
```

---

## ErrorInterceptor

```ts
if (body?.code && ERROR_MESSAGES[body.code]) {
  return ERROR_MESSAGES[body.code];
}
```

---

# ERROR_MESSAGES

```ts
AUTH_INVALID_CREDENTIALS:
'Usuario o contraseña incorrectos'

USER_NOT_FOUND:
'El usuario no fue encontrado'

SCHOOL_ALREADY_EXISTS:
'La institución ya se encuentra registrada'
```

---

# Regla para Nuevos Módulos

Cada nuevo módulo debe:

1. Crear sus ErrorCode.
2. Lanzar excepciones especializadas.
3. Nunca usar mensajes para controlar lógica.
4. Exponer siempre:

    * status
    * error
    * code
    * message
    * path
    * timestamp

---

# Lección Aprendida

Se detectó que usar:

```java
IllegalArgumentException
IllegalStateException
NoSuchElementException
```

provocó que errores distintos terminaran agrupados bajo:

```java
BUSINESS_RULE_VIOLATION
```

impidiendo que Angular pudiera mostrar mensajes específicos.

La solución adoptada es utilizar excepciones tipadas con `ErrorCode` propio, permitiendo una comunicación estable entre Backend y Frontend basada en códigos y no en textos.
