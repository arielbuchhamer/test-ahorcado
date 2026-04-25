# Test Ahorcado

Proyecto base para el desarrollo de una aplicacion del juego del ahorcado, pensado como trabajo practico de practicas de desarrollo y CI/CD.

## Estado actual

El repositorio arranca con una base minima de backend en Java para comenzar a trabajar con TDD desde el inicio.

### Tecnologias definidas

- Backend: Java 21
- Build tool: Maven
- Testing backend: JUnit 5
- CI: GitHub Actions
- Code coverage: JaCoCo
- Frontend: React

## Estructura del proyecto

```text
test-ahorcado/
|-- .github/
|   `-- workflows/
|       `-- ci-compile.yml
|-- backend/
|   |-- pom.xml
|   `-- src/
|       |-- main/java/com/testahorcado/backend/
|       `-- test/java/com/testahorcado/backend/
|-- .gitignore
`-- README.md
```

## Backend

El backend fue inicializado con Maven y configurado para trabajar con Java 21.

Incluye:

- un `pom.xml` base
- soporte para `JUnit 5`
- una clase inicial de ejemplo
- un test unitario inicial

## Unit Tests

Los unit tests del backend se implementan con **JUnit 5**.

Actualmente se utilizan para validar de forma automatica reglas basicas del juego del ahorcado, siguiendo un enfoque incremental orientado a TDD.

## CI

El proyecto utiliza **GitHub Actions** como servidor de integracion continua.

Actualmente, el workflow:

- compila el backend con Maven
- ejecuta los unit tests automaticamente
- se dispara en cada `push` y `pull request` sobre `main`

La configuracion del pipeline se encuentra en `.github/workflows/ci-compile.yml`.

## Code Coverage

La cobertura de codigo del backend se mide con **JaCoCo**.

Al ejecutar los tests, Maven genera el reporte de coverage y GitHub Actions lo publica como artifact en cada ejecucion del pipeline.

El reporte HTML se genera en:

```text
backend/target/site/jacoco/
```

## Como ejecutar los tests

Desde la carpeta `backend`, ejecutar:

```bash
mvn test
```

## Objetivo de esta base

Esta base permite comenzar con:

- desarrollo guiado por tests
- evolucion incremental del backend
- futura integracion con frontend React
- incorporacion posterior de analisis estatico
