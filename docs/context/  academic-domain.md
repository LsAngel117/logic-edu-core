# Estructura académica del dominio

## 1. Objetivo

Definir una estructura académica flexible para múltiples escuelas dentro de la misma plataforma, soportando distintos modelos de organización:

- semestral
- trimestral
- anual por niveles
- con o sin cortes
- con o sin evaluación subperiódica

La meta es que cada `School` pueda configurar su propia estructura sin romper el modelo general ni amarrarse a una única lógica académica.

---

## 2. Principio de diseño

La arquitectura debe separar tres ideas distintas:

- **Estructura académica**: cómo organiza una escuela su formación.
- **Periodo académico**: la unidad principal de tiempo académico.
- **Evaluación interna**: cortes, parciales u otras subdivisiones del periodo.

Esto evita mezclar conceptos que no tienen el mismo nivel semántico.

---

## 3. Decisiones de vocabulario

Se recomienda usar estos nombres en el dominio:

- `School`: escuela
- `Branch`: sede
- `AcademicStructure`: configuración académica de una escuela
- `AcademicLevel`: nivel o año de formación
- `AcademicPeriod`: periodo principal, por ejemplo semestre o trimestre
- `EvaluationPeriod`: cortes o subdivisiones evaluativas, solo cuando apliquen
- `Subject`: materia o asignatura
- `Group`: grupo o curso ofertado
- `Enrollment`: matrícula
- `Schedule`: horario
- `Attendance`: asistencia
- `Assessment`: evaluación o actividad evaluable
- `Grade`: nota o resultado

---

## 4. Propuesta de estructura de paquetes

```text
domain/
  academic/
    structure/
    level/
    period/
    evaluation/
    subject/
    group/
    enrollment/
    schedule/
    attendance/
    assessment/
    grading/
    curriculum/
```

---

## 5. Responsabilidad de cada concepto

### 5.1 `AcademicStructure`
Representa la configuración académica de una escuela.

Debe indicar, por ejemplo:

- tipo de organización
- cantidad de niveles
- cantidad de periodos por nivel
- si existen cortes
- cantidad de materias por periodo
- horas por materia
- vigencia o versión de la configuración

Ejemplo:

- escuela de teología:
  - 5 niveles
  - 4 trimestres por nivel
  - 2 materias por trimestre
  - 10 horas por materia
  - sin cortes

- escuela semestral:
  - N niveles
  - 2 semestres por nivel
  - 3 cortes por semestre
  - con cortes

---

### 5.2 `AcademicLevel`
Representa un nivel formativo o año académico.

Ejemplos:

- Nivel 1
- Nivel 2
- Año 1
- Año 2

Este concepto no debe confundirse con un periodo.  
Un nivel agrupa periodos.

---

### 5.3 `AcademicPeriod`
Representa el bloque temporal principal dentro del nivel.

Puede ser:

- semestre
- trimestre
- ciclo
- módulo
- bimestre
- anual

Este concepto es el eje temporal principal del dominio académico.

---

### 5.4 `EvaluationPeriod`
Representa subunidades de evaluación dentro de un periodo académico, cuando la estructura lo requiera.

Ejemplo:

- Corte 1
- Corte 2
- Corte 3

No todas las escuelas necesitan este concepto.  
Debe ser opcional según la estructura académica.

---

### 5.5 `Subject`
Representa la materia o asignatura.

Debe contener reglas propias como:

- nombre
- código
- intensidad horaria
- créditos, si aplica
- estado

---

### 5.6 `Group`
Representa la oferta concreta de una materia en un periodo y nivel.

Ejemplo:

- Materia: Teología Bíblica
- Periodo: Trimestre 1
- Grupo: Grupo A
- Docente asignado
- Sede
- Horario

`Group` no es la materia en sí misma, sino la instancia ofertada.

---

### 5.7 `Enrollment`
Representa la matrícula de un estudiante en un `Group` o en una combinación académica definida por el negocio.

Debe manejar:

- estudiante
- grupo
- estado de matrícula
- fechas
- resultado o seguimiento

---

### 5.8 `Schedule`
Representa el horario del grupo.

Debe permitir:

- día
- hora de inicio
- hora de fin
- sede
- aula
- docente

---

### 5.9 `Attendance`
Representa el control de asistencia por sesión.

Debe permitir:

- estudiante
- grupo
- fecha
- estado de asistencia
- observaciones

---

### 5.10 `Assessment` y `Grade`
Representan actividades evaluativas y sus resultados.

Ejemplos:

- quices
- parciales
- trabajos
- exposiciones
- nota final

---

## 6. Casos de uso principales

### 6.1 Configurar estructura académica
Un `SchoolAdmin` define la estructura académica de su escuela.

Ejemplos:

- semestral
- trimestral
- anual por niveles
- con cortes
- sin cortes

### 6.2 Crear niveles
Se crean los niveles o años académicos según la estructura.

### 6.3 Crear periodos académicos
Se crean los periodos principales para cada nivel.

### 6.4 Crear grupos
Se ofertan materias en un periodo y sede específicos.

### 6.5 Matricular estudiantes
Los estudiantes se inscriben en los grupos.

### 6.6 Registrar horarios, asistencia y notas
Se operan los procesos académicos diarios.

---

## 7. Reglas de dominio recomendadas

### 7.1 Una escuela puede tener su propia estructura
Cada `School` debe poder definir cómo funciona su modelo académico.

### 7.2 Un periodo no siempre tiene cortes
Los cortes deben ser opcionales según el tipo de estructura.

### 7.3 Un `Group` depende de un `Subject` y de un `AcademicPeriod`
No debe existir de forma aislada.

### 7.4 La matrícula depende del grupo y del estudiante
No debe mezclarse con `School` ni con `Subject`.

### 7.5 La estructura debe ser versionable
Si una escuela cambia de semestral a trimestral, la estructura nueva no debe romper el histórico.

---

## 8. Propuesta de clases reales del dominio

### `AcademicStructure`
Value objects y datos sugeridos:

- `AcademicStructureId`
- `SchoolId`
- `StructureType`
- `levelsCount`
- `periodsPerLevel`
- `evaluationPeriodsPerPeriod`
- `subjectsPerPeriod`
- `hoursPerSubject`
- `active`
- `createdAt`
- `updatedAt`

### `AcademicLevel`
- `AcademicLevelId`
- `SchoolId`
- `name`
- `number`
- `status`
- `createdAt`
- `updatedAt`

### `AcademicPeriod`
- `AcademicPeriodId`
- `AcademicLevelId`
- `PeriodType`
- `name`
- `sequence`
- `startDate`
- `endDate`
- `status`
- `createdAt`
- `updatedAt`

### `EvaluationPeriod`
- `EvaluationPeriodId`
- `AcademicPeriodId`
- `name`
- `sequence`
- `weight`
- `startDate`
- `endDate`
- `status`

### `Subject`
- `SubjectId`
- `SchoolId`
- `code`
- `name`
- `description`
- `hours`
- `status`

### `Group`
- `GroupId`
- `SubjectId`
- `AcademicPeriodId`
- `BranchId`
- `teacherId`
- `code`
- `capacity`
- `status`

### `Enrollment`
- `EnrollmentId`
- `UserId`
- `GroupId`
- `status`
- `enrolledAt`
- `updatedAt`

---

## 9. Orden recomendado de implementación

### Fase 1: estructura académica
Primero modelar:

- `AcademicStructure`
- `AcademicLevel`
- `AcademicPeriod`
- `EvaluationPeriod`

Esto define las reglas base del sistema.

### Fase 2: catálogo académico
Luego crear:

- `Subject`

### Fase 3: operación académica
Después modelar:

- `Group`
- `Schedule`
- `Enrollment`

### Fase 4: seguimiento académico
Finalmente:

- `Attendance`
- `Assessment`
- `Grade`

---

## 10. Regla clave para no perder coherencia

No mezclar en una sola entidad lo que pertenece a niveles distintos.

Ejemplos de lo que conviene evitar:

- poner cortes dentro de `School`
- poner asistencia dentro de `Subject`
- poner matrícula dentro de `Group`
- poner horarios dentro de `School`
- poner la estructura académica como simples banderas sueltas

Cada concepto debe vivir donde corresponde.

---

## 11. Cómo encaja con la arquitectura actual

La arquitectura que ya llevas puede mantenerse así:

```text
domain/
  user/
  school/
  branch/
  membership/
  academic/
```

Y dentro de `application`:

```text
application/
  academic/
        /   
        dto/
        mapper/
        port/
        usecase/
        policy/
```

La aplicación coordina:

- validación de estructura
- existencia de School y Branch
- reglas de creación
- persistencia mediante puertos

El dominio conserva:

- reglas puras
- inmutabilidad
- invariantes de cada agregado

---

## 12. Recomendación final

Para este punto del proyecto, lo más importante no es construir muchas clases, sino definir bien los límites.

La secuencia correcta es:

1. definir la estructura académica
2. definir el significado de nivel y periodo
3. definir si existen cortes
4. definir la oferta académica con `Group`
5. definir matrícula, horario, asistencia y notas

Si se respeta ese orden, el dominio queda flexible, escalable y alineado con una arquitectura hexagonal limpia y sostenible.
