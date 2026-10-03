# api-utils

Shared Kotlin utility library for Spring Boot APIs. Distributed via [JitPack](https://jitpack.io).

## What's inside

```
com.progmise.utils
├── config/          Auto-configuration (ApiExceptionHandler, FeatureToggleHelper beans)
├── delivery/        ListPaginationDTO (HAL pagination links) + PaginationRequestBuilder
├── dto/             ApiError + ErrorsResponse — {"errors":[{"code","message","level","description"}]}
├── enums/           ErrorLevel, LinkRef, EnumCompanion/EnumUtil helpers
├── exception/       ExceptionCode, RequestException, BadRequestException, ApiExceptionHandler
├── infrastructure/  Cache iface, RCache (Redisson), NoOpCache, FeatureToggleStateRepository
│                    (caching wrapper over JDBC StateRepository), FeatureToggleHelper
├── util/            Constants, extension functions, JsonMapper, generate*Exception helpers
└── validator/       Validator iface, CompositeValidator, BaseValidator + generic validators
                     (Integer/Numeric/Decimal/MajorOrEqual/Date/Length)
```

## Usage

Add the JitPack repository and the dependency:

```kotlin
repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    implementation("com.github.progmise:api-utils:0.1.0")
}
```

Beans are auto-configured when the jar is on the classpath:

- `ApiExceptionHandler` — `@RestControllerAdvice` mapping `RequestException` /
  `BadRequestException` / malformed requests / unknown errors to the shared
  `{"errors":[{code,message,level,description}]}` contract. Register your own
  `@RestControllerAdvice` (or bean) for domain exceptions — or declare an
  `ApiExceptionHandler` bean to fully override it.
- `FeatureToggleHelper` — created automatically when a Togglz `FeatureManager`
  bean exists. Exposes `isActive(feature: Enum<*>)` / `isActive(name: String)`.

Everything else (validators, `RCache`, `FeatureToggleStateRepository`, pagination)
is instantiated explicitly — see the consuming projects for wiring examples.

## Consumers

- `amortization-api` — schedules, validators, cache-aside, feature toggles.

## Development

```bash
./gradlew build                # compile + ktlint + tests
./gradlew publishToMavenLocal  # install to ~/.m2 for local consumers
```

## Releasing

Tag a commit and push the tag; JitPack builds the artifact on first request.

```bash
git tag 0.1.0 && git push origin 0.1.0
```

Bump `version` in `build.gradle.kts` to match the tag.
