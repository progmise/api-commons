# api-utils

Shared Java utility library for Spring Boot APIs. Published to **Maven Central**
as `io.github.progmise:api-utils`.

## What's inside

```
io.github.progmise.utils
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
tokens: `cache.get(key, Schedule.class)` or
`cache.getObject(key, new TypeReference<List<X>>() {})`.

## Usage

```kotlin
dependencies {
    implementation("io.github.progmise:api-utils:0.2.0")
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

- `amortization-api` — schedules, validators, cache-aside, feature toggles.

## Development

```bash
./gradlew build   # compile + unit tests
```

## Releasing

Publishing is fully automated by GitHub Actions — `build.gradle.kts` carries **no**
publishing configuration. The workflow applies `.github/publish.init.gradle.kts`,
which injects the `com.vanniktech.maven.publish` plugin, GPG signing and all POM
metadata at publish time:

```bash
git tag 0.3.0 && git push origin 0.3.0
# → tag push triggers .github/workflows/publish.yml → publishToMavenCentral
```

Bump `version` in `build.gradle.kts` to match the tag.

### One-time setup

Repository secrets required by the workflow:

| Secret | Value |
|---|---|
| `SONATYPE_USERNAME` / `SONATYPE_TOKEN` | Central Portal user token (Account → Generate User Token) |
| `GPG_PRIVATE_KEY` | ASCII-armored private key used for signing |
| `GPG_PASSPHRASE` | The key's passphrase |

The namespace `io.github.progmise` must be verified in the Central Portal
(automatic when the account is linked to the `progmise` GitHub account).
