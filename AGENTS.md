# AGENTS.md

Guide for working on **api-utils** — a shared Kotlin library for Spring Boot APIs,
distributed via JitPack (`com.github.progmise:api-utils:<tag>`).

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
| `enums` | `ErrorLevel`, `LinkRef`, `EnumCompanion`/`EnumUtil` |
| `exception` | `ExceptionCode`, `RequestException`, `BadRequestException`, `ApiExceptionHandler` |
| `infrastructure` | `Cache` iface, `RCache` (Redisson), `NoOpCache`, `FeatureToggleStateRepository`, `FeatureToggleHelper` |
| `util` | `Constants`, extensions (`logger()`, `typeRef()`, `ifNotNullAndBlank`, ...), `JsonMapper`, `generate*Exception` helpers |
| `validator` | `Validator` iface, `CompositeValidator`, `BaseValidator`, generic validators |

## Conventions

- Public APIs must be usable from Kotlin **and** reasonable from Java: prefer classes
  and objects; extension functions are fine as *helpers*, not as the only way to do
  something.
- Auto-configured beans: always `@ConditionalOnMissingBean` so consumers can override;
  `@ConditionalOnBean`/`@ConditionalOnProperty` for optional integrations.
- Errors: consumers throw `RequestException`/`BadRequestException`; the handler maps
  them to the shared error contract. Domain exceptions stay in the app with their
  own `@RestControllerAdvice`.
- `RCache` must stay **fail-open**: Redis down ⇒ null/false, never throw.
- Dependencies: public types in signatures ⇒ `api(...)`; internal only ⇒ `implementation(...)`.
- Never add employer-specific/proprietary code, internal endpoints, or credentials.

## Verify before done

```bash
./gradlew build                # compile + ktlint + unit tests
./gradlew publishToMavenLocal  # local install for consumer testing
```

## Release

Bump `version` in `build.gradle.kts`, tag `git tag X.Y.Z`, push the tag.
Consumers pin the tag via `com.github.progmise:api-utils:X.Y.Z`.
Consumers may also keep `mavenLocal()` for local iteration (`publishToMavenLocal`).

## Consumers

- `amortization-api` (`C:\Users\Leonel\Documents\kotlin-workspace\amortization-api`)

When promoting code out of a consumer: move the generic shape here, keep domain
logic in the app, and update **all** imports (`.kt`, tests, yaml, docs).
