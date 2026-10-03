# AGENTS.md

Guide for working on **api-utils** — a shared Java library for Spring Boot APIs,
published to Maven Central as `io.github.progmise:api-utils`.

## Golden rule

This library is shared infrastructure. Every public API change can break consumers.
Keep it **generic** — no domain logic, no app-specific names (nothing like
`Schedule`, `Loan`, `Customer` belongs here). When in doubt, leave it in the app.

## Package map

| Package | Holds |
|---|---|
| `config` | Spring Boot auto-config (`@AutoConfiguration`) — registered via `META-INF/spring/...AutoConfiguration.imports` |
| `delivery` | `ListPaginationDTO` (HAL links) + request builders (e.g. `PaginationRequestBuilder`) |
| `dto` | `ApiError`, `ErrorsResponse` — the `{"errors":[{code,message,level,description}]}` contract |
| `enums` | `ErrorLevel`, `LinkRef` |
| `exception` | `ExceptionCode`, `RequestException`, `BadRequestException`, `ApiExceptionHandler` |
| `infrastructure` | `Cache` iface, `RCache` (Redisson), `NoOpCache`, `FeatureToggleStateRepository`, `FeatureToggleHelper` |
| `util` | `Constants`, `Extensions`, `JsonMapper`, `ExceptionCodeGenerators` |
| `validator` | `Validator` iface, `CompositeValidator`, `BaseValidator`, generic validators |

## Conventions

- **Pure Java public API** — the lib is consumed by Kotlin apps too, so avoid
  anything that maps awkwardly: no Kotlin-only constructs, prefer `Class<T>` /
  Jackson `TypeReference<T>` type tokens for generics.
- Auto-configured beans: always `@ConditionalOnMissingBean` so consumers can
  override; `@ConditionalOnBean`/`@ConditionalOnProperty` for optional integrations.
- Errors: consumers throw `RequestException`/`BadRequestException`; the handler
  maps them to the shared error contract. Domain exceptions stay in the app with
  their own `@RestControllerAdvice`.
- `RCache` must stay **fail-open**: Redis down ⇒ null/false, never throw.
- Dependencies: public types in signatures ⇒ `api(...)`; internal only ⇒
  `implementation(...)`.
- Never add employer-specific/proprietary code, internal endpoints, or credentials.

## Verify before done

```bash
./gradlew build    # compile + unit tests

# validate the full publishing pipeline locally (requires the signing env vars):
export ORG_GRADLE_PROJECT_signingInMemoryKey="$(cat key.asc)"
export ORG_GRADLE_PROJECT_signingInMemoryKeyPassword="..."
./gradlew publishToMavenLocal -I .github/publish.init.gradle.kts
```

## Release

`build.gradle.kts` intentionally has **zero** publishing config — it is injected
in CI by `.github/publish.init.gradle.kts` (applied with Gradle's `-I` flag).
Pushing a tag runs `.github/workflows/publish.yml` → `publishToMavenCentral`
with `SONATYPE_*`/`GPG_*` repository secrets. Bump `version` to match the tag.

## Consumers

- `amortization-api` (`C:\Users\Leonel\Documents\kotlin-workspace\amortization-api`)

When promoting code out of a consumer: move the generic shape here, keep domain
logic in the app, and update **all** imports (sources, tests, yaml, docs).
