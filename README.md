# RestAssured BDD API Automation Framework

A behaviour-driven API test automation framework built with **RestAssured**, **Cucumber**, and the **JUnit Platform**, targeting REST API validation with a clean layered architecture. The suite runs in parallel, is containerised with Docker, and is wired into a Jenkins CI pipeline.

The reference API under test is the Places API (add / get / delete place), but the architecture is designed to extend to any REST API by adding new client and POJO classes.

---

## Tech Stack

| Concern | Technology |
|---|---|
| API testing | RestAssured 5.5.0 |
| BDD layer | Cucumber 7.34.3 (Gherkin + step definitions) |
| Test runner / execution | JUnit Platform (Jupiter) 5.11.x |
| Dependency injection | PicoContainer (cucumber-picocontainer) |
| JSON serialisation | Jackson (jackson-databind) |
| Logging | SLF4J + Logback |
| Assertions | Hamcrest + RestAssured |
| Reporting | maven-cucumber-reporting (masterthought) |
| Build / dependencies | Maven |
| Containerisation | Docker |
| CI/CD | Jenkins (pipeline-as-code via Jenkinsfile) |

---

## Architecture

The framework follows a layered design with clear separation of concerns, so the interaction logic is abstracted away from the test steps and the framework scales by adding new clients rather than rewriting tests.

```
src
├── main/java
│   ├── Pojo/                     # Data models for request/response bodies
│   │   ├── AddPlaceSerialiser    #   - serialised to JSON request bodies
│   │   ├── DeletePlaceSerialiser
│   │   └── Location
│   └── Utilities/
│       └── ReusableMethods       # Generic reusable helpers
│
└── test/java
    ├── apiautomation/
    │   └── PlacesApiClient       # SERVICE/CLIENT LAYER - encapsulates the API calls
    ├── StepDefinitions/
    │   ├── GoogleStepDefinition  # Step definitions (the Gherkin glue)
    │   └── Hooks                 # Before/After scenario hooks
    ├── Resources/
    │   ├── Utils                 # Request spec builder, property loading
    │   ├── ScenarioContext       # Per-scenario shared state (injected via DI)
    │   ├── TestDataBuilder       # Builds test data / payload objects
    │   ├── ResourceConstants
    │   └── features/
    │       └── placeValidations.feature   # Gherkin scenarios
    └── cucumberOptions/
        └── RunCucumberTest       # JUnit Platform suite runner
```

### Layer responsibilities

- **Data layer (POJOs + TestDataBuilder)** — POJO classes model the JSON request/response bodies; Jackson serialises POJOs to JSON for requests and deserialises responses back into objects. The TestDataBuilder constructs payload objects with test data.
- **Service / client layer (`PlacesApiClient`)** — encapsulates the actual REST calls so step definitions stay clean and readable. This is the API equivalent of a Page Object in a UI framework: it hides *how* the system is called.
- **Utilities / config (`Utils`, properties files)** — builds the RestAssured request specification and loads environment-specific config. Environment is selected at runtime via `-Denv`.
- **Test layer (features + step definitions)** — feature files hold the Gherkin scenarios; step definitions implement the code behind each step. Run via the JUnit Platform suite.
- **Dependency injection (PicoContainer)** — creates a fresh `ScenarioContext` and client instance per scenario and injects them into steps/hooks through constructors, giving test isolation and parallel safety.

---

## Prerequisites

- Java JDK 20+ (the Docker image uses Temurin 21)
- Maven 3.9+
- Docker (for containerised runs)
- Jenkins with the Docker, HTML Publisher, and Pipeline plugins (for CI)

---

## Running the tests

### Locally with Maven

Run the full suite (default environment and tags):
```bash
mvn clean verify
```

Select an environment (loads the matching `<env>.properties` file):
```bash
mvn clean verify -Denv=dev
```

Filter by Cucumber tags:
```bash
mvn clean verify -Dcucumber.filter.tags="@endtoend"
mvn clean verify -Dcucumber.filter.tags="@negative"
```

### Environment configuration

Environment-specific values (base URL, API key, etc.) live in property files under `src/test/resources`:

- `global.properties` (default)
- `dev.properties`
- `test.properties`

The environment is chosen at runtime with `-Denv=<name>` (e.g. `-Denv=dev` loads `dev.properties`). This keeps the same suite running across environments by changing a flag rather than editing code.

---

## Parallel Execution

The suite runs **scenarios in parallel** for faster execution.

Parallelism is enabled through `src/test/resources/junit-platform.properties`:
```properties
cucumber.execution.parallel.enabled=true
cucumber.execution.parallel.mode.default=concurrent
cucumber.execution.parallel.config.strategy=dynamic
```

- `parallel.enabled=true` — turns on parallel execution
- `mode.default=concurrent` — runs scenarios concurrently
- `config.strategy=dynamic` — sizes the thread pool to available CPU cores (use `fixed` + `fixed.parallelism=N` for an explicit count)

**Thread-safety:** the framework was made thread-safe *before* parallelism was enabled. Shared static state was removed — the request specification is built fresh per call, and the log capture buffer is per-instance (per scenario via PicoContainer) rather than static. This prevents race conditions across concurrent scenarios.

---

## Test Tagging Strategy

Scenarios are tagged so subsets can be run independently:

- `@endtoend` — the end-to-end / regression set
- `@smoke` — a fast critical-path subset (intended for per-commit runs)
- `@negative` — error-path / negative scenarios
- plus scenario-specific tags (`@addplace`, `@deleteplace`, etc.)

Select at runtime with `-Dcucumber.filter.tags`, e.g. `"@endtoend or @negative"` or `"@smoke"`.

---

## Reporting

Two reports are produced:

1. **Cucumber HTML/JSON** (from the runner) — `target/cucumber-reports/cucumber.html` and `cucumber.json`.
2. **Masterthought dashboard** (`maven-cucumber-reporting`) — a rich HTML dashboard with pass/fail charts and tag/step/failure breakdowns, generated during the `verify` phase at `target/cucumber-html-reports/overview-features.html`.

> Note: when viewing the masterthought report through Jenkins, the Content Security Policy may block its JavaScript/CSS (charts won't render). Relax it via the Jenkins Script Console:
> ```groovy
> System.setProperty("hudson.model.DirectoryBrowserSupport.CSP", "sandbox allow-scripts; default-src 'self' 'unsafe-inline' 'unsafe-eval' data:;")
> ```

---

## Docker

The framework is containerised so it runs identically on any Docker host, independent of the host's JDK/Maven.

**Dockerfile** (project root):
```dockerfile
FROM maven:3.9-eclipse-temurin-21
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
ENTRYPOINT ["mvn"]
CMD ["clean", "verify"]
```

`ENTRYPOINT ["mvn"]` + `CMD ["clean", "verify"]` means the default run is `mvn clean verify`, but runtime arguments cleanly **replace** the CMD (rather than appending), so you can override goals, environment, and tags.

**Build and run locally:**
```bash
docker build -t restassured-bdd .

# default run
docker run --rm restassured-bdd

# override env and tags
docker run --rm restassured-bdd clean verify -Denv=dev -Dcucumber.filter.tags="@smoke"
```

---

## CI/CD (Jenkins)

The `Jenkinsfile` defines a pipeline-as-code that builds a Docker image, runs the suite inside the container, extracts the reports, and publishes them.

**Pipeline flow:**
1. **Determine Tags** — picks the tag set (smoke for push-triggered builds, full regression for the nightly run)
2. **Checkout** — pulls the latest code from Git
3. **Build Docker Image** — `docker build -t restassured-bdd:${BUILD_NUMBER} .`
4. **Run Tests in Docker** — runs the suite in the container, then `docker cp` extracts `target/` out so the reports survive the container teardown
5. **Post actions** — publishes JUnit results and the Cucumber HTML report

**Triggers:**
- `pollSCM('H/2 * * * *')` — polls Git for new commits (~every 2 min) and builds on change. (Local Jenkins can't receive GitHub webhooks; a webhook would be used on a reachable CI server for instant triggering.)
- `cron('H 2 * * *')` — nightly full-regression run.

**Parameters:**
- `ENVIRONMENT` — which properties file to use (`-Denv`)
- `TAGS` — override the Cucumber tag filter (blank = auto-select by trigger)

**Report extraction note:** because tests run inside the container, reports are generated at `/app/target` and copied out with `docker cp` into the Jenkins workspace before publishing. The workspace `target/` is cleared first to avoid a nested `target/target` directory.

---

## Key Design Decisions

- **Service/client layer** keeps step definitions clean and the framework extensible — add a new client + POJOs for a new API without touching existing tests.
- **Dependency injection (PicoContainer)** gives per-scenario isolation and is what makes parallel execution safe.
- **No shared static state** — request spec built fresh per call; log buffer per-instance. Thread-safety precedes parallelism.
- **Environment-agnostic config** — `-Denv` selects per-environment properties at runtime.
- **JUnit Platform over JUnit 4** — chosen for first-class scenario-level parallel support via the Cucumber JUnit Platform engine.
- **Containerised execution** — environment defined in the Dockerfile, so the suite runs identically across machines; CI agents only need Docker, not the full toolchain.

---

## Known Gaps / Future Enhancements

- **JSON schema validation** — currently responses are validated via POJO deserialisation and field assertions; adding schema validation against the OpenAPI/Swagger spec would validate the full contract, not just modelled fields.
- **Token caching** — a thread-safe `TokenManager` pattern is available as a documented capability but not wired into the live flow, since the reference API uses a static key rather than tokens. It would activate against a token-based, rate-limited auth API.
- POJOs currently live in `src/main` while the rest of the framework is in `src/test` — could be consolidated.

---

## Repository

`https://github.com/gauthamgits/RestApiFramework`
