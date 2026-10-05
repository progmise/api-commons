# AGENTS.md

Guide for working on **api-commons** — a shared Java library for Spring Boot APIs,
published to Maven Central as `io.github.progmise:api-commons`.

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
- Dependencies: this is a **Maven** project (`pom.xml`) — types used in public
  signatures stay `compile` scope; servlet API is `provided`; tests are `test`.
- Never add employer-specific/proprietary code, internal endpoints, or credentials.

## Verify before done

```bash
./mvnw -B verify    # compile + unit tests + JaCoCo report (target/site/jacoco/)

# validate the full publishing pipeline locally (requires GPG key in the agent):
gpg --batch --import key.asc
./mvnw -B -Prelease deploy -DaltDeploymentRepository=local::file:./target/mvn-local
```

## Branches

GitFlow: `development` is default, `main` holds releases. Name work branches
`<type>/<snake_description>` (e.g. `feature/pagination_links`):

| Prefix | Use | Base |
|---|---|---|
| `feature/` | new functionality | `development` |
| `fix/` | bug fix | `development` |
| `bug/` | defect found in existing code | `development` |
| `hotfix/` | urgent fix on released code | `main` → merge back to `development` |
| `chore/` | tooling, deps, config | `development` |
| `docs/` | documentation only | `development` |
| `refactor/` | internal change, no API diff | `development` |
| `sync/` | `development` → `main` syncs | — |

## Release

Publishing config lives in `pom.xml` (`central-publishing` plugin, sources and
javadoc jars); the `release` profile adds GPG signing — CI activates it only on
the publish step. Branching is GitFlow: `development` is the default branch
(all work is PR'd there), `main` holds releases. To release: bump `<version>`
in `pom.xml`, merge
`development` → `main` via PR, then run the **Release** workflow manually on
`main` — it validates the version, runs the CI checks (`ci.yml`), publishes to
Maven Central with the `SONATYPE_*`/`GPG_*` repository secrets and creates the
tag + GitHub Release. Development versions of any merged commit resolve via
JitPack (`com.github.progmise:api-commons:<sha>`).

## Consumers

- `amortization-api` (`C:\Users\Leonel\Documents\kotlin-workspace\amortization-api`)

When promoting code out of a consumer: move the generic shape here, keep domain
logic in the app, and update **all** imports (sources, tests, yaml, docs).
