# api-commons

Shared Java utility library for Spring Boot APIs. Published to **Maven Central**
as `io.github.progmise:api-commons`.

## What's inside

```
io.github.progmise.commons
├── config/          Auto-configuration (ApiExceptionHandler, FeatureToggleHelper beans)
├── delivery/        ListPaginationDTO (HAL pagination links) + PaginationRequestBuilder
├── dto/             ApiError + ErrorsResponse — {"errors":[{"code","message","level","description"}]}
├── enums/           ErrorLevel, LinkRef
├── exception/       ExceptionCode, RequestException, BadRequestException, ApiExceptionHandler
├── infrastructure/  Cache iface, RCache (Redisson), NoOpCache, FeatureToggleStateRepository
│                    (caching wrapper over JDBC StateRepository), FeatureToggleHelper
├── util/            Constants, Extensions, JsonMapper, ExceptionCodeGenerators
└── validator/       Validator iface, CompositeValidator, BaseValidator + generic validators
                     (Integer/Numeric/Decimal/MajorOrEqual/Date/Length)
```

The API surface is plain Java (no Kotlin-only constructs), so it can be consumed
idiomatically from Java and Kotlin. Generic deserialization uses explicit type
tokens: `cache.get(key, User.class)` or
`cache.getObject(key, new TypeReference<List<X>>() {})`.

## Usage

```kotlin
dependencies {
    implementation("io.github.progmise:api-commons:0.3.0")
}
```

Beans are auto-configured when the jar is on the classpath:

- `ApiExceptionHandler` — `@RestControllerAdvice` mapping `RequestException` /
  `BadRequestException` / malformed requests / unknown errors to the shared
  `{"errors":[{code,message,level,description}]}` contract. Register your own
  `@RestControllerAdvice` (or bean) for domain exceptions — or declare an
  `ApiExceptionHandler` bean to fully override it.
- `FeatureToggleHelper` — created automatically when a Togglz `FeatureManager`
  bean exists. Exposes `isActive(Enum<?>)` / `isActive(String)`.

Everything else (validators, `RCache`, `FeatureToggleStateRepository`, pagination)
is instantiated explicitly — see the consuming projects for wiring examples.

## Consumers

- `loans-api` — error contract, validators, `RCache` cache-aside, `FeatureToggleHelper` (`0.3.0` via Central).
- `java-maven-api-template` — same shared infra for generated services (`0.3.0` via Central).
- `amortization-api` — schedules, validators, cache-aside, feature toggles (JitPack pin; Central migration pending).

## Development

```bash
./mvnw -B verify   # compile + unit tests + JaCoCo report
```

## Releasing

Publishing is fully automated by GitHub Actions — all artifact metadata and the
`central-publishing` + sources/javadoc plugins live in `pom.xml`; signing lives
in the `release` profile (activated by the release workflow only):

Branching is GitFlow: `development` is the default branch, `main` holds
releases.

1. Bump `<version>` in `pom.xml` on `development`, PR `development` →
   `main` and merge.
2. Run the **Release** workflow manually on `main` (Actions → Release → *Run
   workflow*) — it validates the version, runs all CI checks, publishes to
   Central and creates the tag + GitHub Release.

Development versions of any merged commit resolve via JitPack:
`com.github.progmise:api-commons:<commit-sha>`.

Every push to `development` or `main` runs the **Integration** workflow (same
checks as the PR `ci.yml`: build + tests + JaCoCo + Trivy + Semgrep).

All workflows are **thin callers** — the pipeline logic lives centrally in
[`progmise/reusable-workflows`](https://github.com/progmise/reusable-workflows)
(`@v1`), so fixes propagate to every library at once.

### One-time setup

Repository secrets required by the workflow:

| Secret | Value |
|---|---|
| `SONATYPE_USERNAME` / `SONATYPE_TOKEN` | Central Portal user token (Account → Generate User Token) |
| `GPG_PRIVATE_KEY` | ASCII-armored private key used for signing |
| `GPG_PASSPHRASE` | The key's passphrase |
| `GRAFANA_OTLP_ENDPOINT` | *(optional, **variable**)* OTLP gateway URL for the `tracing` job (Grafana Cloud free tier) |
| `GRAFANA_OTLP_AUTH` | *(optional)* `base64("<instance-id>:<api-token>")` for that gateway |

The namespace `io.github.progmise` must be verified in the Central Portal
(automatic when the account is linked to the `progmise` GitHub account).

Every CI run also uploads its reports as artifacts (30-day retention): test +
JaCoCo HTML, `trivy-results.json`, `semgrep-results.json`, `japicmp.xml`
(API compat). The `tracing` job
emits OTel spans (real per-job durations) and gauges (`ci.coverage.percent`,
`ci.trivy.findings`, `ci.semgrep.findings`, `ci.job.duration_seconds`,
`ci.jobs.*`) to Grafana Cloud when the `GRAFANA_OTLP_*` variable/secret are set.

## Not ported (possible future work)

Jobs the reference pipeline had that were skipped because they lack a
worthwhile free equivalent today:

- **Threat Modeling validation** — enterprise-internal, no free equivalent.
- **SCQA** — SonarCloud is free for *public* repos only; could be enabled here
  later.
- **Required status checks** — doable for free; pending the first CI run to
  pin the exact check names in the branch protection rules.
- **Commit convention validation** — commitlint is free; wire it if a
  conventional-commit convention is adopted.
