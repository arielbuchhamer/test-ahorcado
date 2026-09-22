# Test Ahorcado

Proyecto base para el desarrollo de una aplicacion del juego del ahorcado, pensado como trabajo practico de practicas de desarrollo y CI/CD.

## Estado actual

La aplicacion esta compuesta por una API REST en Java con la logica del juego y una UI web en React que la consume.

### Tecnologias definidas

- Backend: Java 21
- Build tool: Maven
- Testing backend: JUnit 5
- CI: GitHub Actions
- Code coverage: JaCoCo
- Analisis estatico continuo: SonarCloud
- API REST: Spring Boot 4
- Frontend: React (Vite)
- Acceptance tests: Cucumber (Gherkin) + Playwright for Java

## Estructura del proyecto

```text
test-ahorcado/
|-- .github/
|   `-- workflows/
|       `-- ci-compile.yml
|-- backend/
|   |-- pom.xml
|   `-- src/
|       |-- main/java/com/testahorcado/backend/      # dominio del juego
|       |-- main/java/com/testahorcado/backend/api/  # API REST
|       |-- main/resources/application.properties
|       `-- test/java/com/testahorcado/backend/
|-- acceptance-tests/
|   |-- pom.xml
|   `-- src/test/
|       |-- java/com/testahorcado/acceptance/        # steps y hooks
|       `-- resources/features/ahorcado.feature      # escenarios en Gherkin
|-- frontend/
|   |-- package.json
|   |-- vite.config.js
|   `-- src/
|       |-- api.js                                   # cliente de la API
|       `-- components/HangmanGame.jsx
|-- .gitignore
`-- README.md
```

## Backend

El backend es una API REST con **Spring Boot 4** y Java 21. Contiene toda la logica del juego, que se desarrolla con TDD.

- `JuegoAhorcado`: reglas del juego (validacion de palabra y letras, vidas, letras usadas, palabra oculta, estado de la partida)
- `api/PartidaController`: expone el juego por HTTP
- `api/PartidaRepository`: guarda las partidas en memoria

### Endpoints

| Metodo | Ruta | Body | Descripcion |
|---|---|---|---|
| `POST` | `/api/partidas` | `{ "palabra": "hola" }` | Crea una partida |
| `GET` | `/api/partidas/{id}` | - | Devuelve el estado de la partida |
| `POST` | `/api/partidas/{id}/letras` | `{ "letra": "a" }` | Intenta una letra |

Respuesta:

```json
{
  "id": "…",
  "palabraOculta": "_ o _ a",
  "vidas": 5,
  "letrasUsadas": ["x", "o", "a"],
  "estado": "EN_JUEGO",
  "palabraSecreta": null,
  "resultado": "ACIERTO"
}
```

- `estado`: `EN_JUEGO`, `GANADA` o `PERDIDA`
- `resultado` (solo al intentar una letra): `ACIERTO`, `FALLO`, `REPETIDA` o `PARTIDA_TERMINADA`
- `palabraSecreta` solo se informa cuando la partida termino
- los datos invalidos devuelven `400` y una partida inexistente `404`, ambos con `{ "error": "mensaje" }`

### Configuracion

| Variable de entorno | Default | Uso |
|---|---|---|
| `PORT` | `8080` | Puerto del servidor |
| `CORS_ALLOWED_ORIGINS` | `http://localhost:5173` | Origenes habilitados para llamar a la API (separados por coma) |

## Frontend

El frontend es una aplicacion React independiente dentro de `frontend/`. No contiene reglas del juego: consume la API del backend y muestra el estado de la partida.

- carga de palabra secreta con input de tipo password
- opcion para mostrar u ocultar la palabra secreta
- ingreso de letras de a una, por input o con el teclado en pantalla
- dibujo simple del ahorcado
- letras usadas, vidas restantes y palabra oculta
- mensajes de error, victoria o derrota devueltos por la API
- reinicio para nueva partida

La URL de la API se configura con la variable `VITE_API_URL` al momento del build. En desarrollo no hace falta: Vite redirige `/api` a `http://localhost:8080`.

## Unit Tests

Los unit tests del backend se implementan con **JUnit 5**.

Se organizan en dos niveles:

- `JuegoAhorcadoTest`: unit tests de las reglas del juego, escritos con TDD
- `PartidaControllerTest`: tests de la API con Spring MockMvc, que validan los endpoints junto con la logica real (service tests)

## Acceptance Tests

Los acceptance tests validan la aplicacion completa desde el navegador (UI + API + logica del juego). Estan en el modulo `acceptance-tests/`, separado del backend para que no afecten la velocidad de los unit tests ni el coverage.

Herramientas:

- **Cucumber**: los escenarios se escriben en Gherkin en español (`ahorcado.feature`)
- **Playwright for Java**: automatiza el navegador (Chromium). Se eligio sobre Selenium porque espera automaticamente a que la UI se actualice luego de cada llamada a la API, lo que evita tests inestables, y ademas es mas rapido
- **JUnit Platform**: ejecuta Cucumber desde Maven

Escenarios:

1. Ganar la partida adivinando todas las letras
2. Perder la partida al quedarse sin vidas
3. Fallar una letra resta una vida
4. Acertar una letra revela todas sus apariciones
5. Repetir una letra no resta vidas
6. Rechazar una palabra secreta con tildes
7. Empezar una nueva partida

### Como ejecutarlos

En CI se ejecutan solos en cada push (ver seccion CI). Para correrlos en local se
necesitan tres terminales, desde la raiz del repositorio:

1. Levantar el backend y esperar el mensaje `Started BackendApplication`:

   ```bash
   cd backend && mvn spring-boot:run
   ```

2. Levantar el frontend:

   ```bash
   cd frontend && npm run dev
   ```

3. Ejecutar los acceptance tests:

   ```bash
   cd acceptance-tests && mvn test
   ```

Para ver el navegador y seguir cada paso a simple vista (con una pausa de 1 segundo entre acciones):

```bash
cd acceptance-tests && HEADLESS=false SLOW_MO=1000 mvn test
```

| Variable de entorno | Default | Uso |
|---|---|---|
| `BASE_URL` | `http://localhost:5173` | URL de la aplicacion a testear |
| `HEADLESS` | `true` | `false` para ver el navegador mientras corren |
| `SLOW_MO` | `0` | Milisegundos de pausa entre cada accion (ej. `1000`) |

Al finalizar, Maven muestra cada escenario con sus pasos y el resumen `Tests run: 7, Failures: 0`. El reporte HTML queda en `acceptance-tests/target/cucumber-report.html`.

La primera ejecucion descarga los navegadores de Playwright. Si el backend o el frontend no estan levantados, los tests fallan en el primer paso con un error de conexion.

## CI

El proyecto utiliza **GitHub Actions** como servidor de integracion continua.

Actualmente, el workflow:

- compila el backend con Maven
- instala dependencias y compila el frontend con Node.js 22 (`npm ci` + `npm run build`)
- ejecuta los unit tests automaticamente
- ejecuta analisis estatico con SonarCloud
- ejecuta los acceptance tests end-to-end (job `acceptance-tests`)
- se dispara en cada `push` y `pull request` sobre `main`

El job `acceptance-tests` corre luego de que compilan backend y frontend. Levanta la
aplicacion completa dentro del runner (el jar del backend en el puerto 8080 y el dev
server de Vite en el 5173, que proxea `/api`), espera a que ambos respondan, instala
Chromium y ejecuta los escenarios de Cucumber. El reporte HTML queda publicado como
artifact `cucumber-report`; si el job falla, se publican tambien los logs de ambos
servidores para poder diagnosticarlo.

La configuracion del pipeline se encuentra en `.github/workflows/ci-compile.yml`.

## Code Coverage

La cobertura de codigo del backend se mide con **JaCoCo**.

Al ejecutar los tests, Maven genera el reporte de coverage y GitHub Actions lo publica como artifact en cada ejecucion del pipeline.

El reporte HTML se genera en:

```text
backend/target/site/jacoco/
```

## Analisis estatico con SonarCloud

El pipeline de CI incluye un paso de analisis estatico usando **SonarCloud** sobre el modulo `backend`.

Para dejarlo operativo en GitHub, hay que configurar:

- un secret llamado `SONAR_TOKEN` con el token generado en SonarCloud
- una variable de repositorio `SONAR_PROJECT_KEY`
- una variable de repositorio `SONAR_ORGANIZATION`

El analisis usa:

- **SonarScanner for Maven**
- cobertura **JaCoCo** en formato XML
- integracion con **GitHub Actions**

Durante el pipeline se ejecuta el analisis luego de compilar, correr tests y generar la cobertura.

## Como ejecutar los tests

Desde la carpeta `backend`, ejecutar:

```bash
mvn test
```

## Como ejecutar la aplicacion

Levantar primero el backend, desde la carpeta `backend`:

```bash
mvn spring-boot:run
```

Luego, en otra terminal, levantar el frontend desde la carpeta `frontend`:

```bash
npm install
npm run dev
```

Abrir `http://localhost:5173`.

Para generar una build de produccion del frontend apuntando a una API desplegada:

```bash
VITE_API_URL=https://mi-backend.example.com npm run build
```

## Objetivo de esta base

Esta base permite comenzar con:

- desarrollo guiado por tests
- evolucion incremental del backend
- incorporacion de analisis estatico en CI con SonarCloud
