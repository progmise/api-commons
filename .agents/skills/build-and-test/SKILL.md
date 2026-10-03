---
name: build-and-test
description: Compile, test and locally install this Java/Gradle shared library (JDK 21, Gradle wrapper, zero publishing config in the build file)
allowed-tools:
  - read
  - exec
  - grep
  - glob
permissions:
  allow:
    - Read(build.gradle.kts)
    - Read(src/**)
  ask:
    - Exec(./gradlew *)
---

# Skill: Build and Test — Java/Gradle shared library

## Description
Step-by-step guide to compile, test and install this shared library. It is a
**Gradle** project built with the wrapper (`./gradlew`) on Java 21.

## When to Use
- Compiling/testing the library for the first time or on a new machine.
- Diagnosing build/test/dependency-resolution failures.
- Producing a local `~/.m2` install so consumers can pick up an unreleased version.

---

## Step 1: Environment
- **JDK 21**. Set `JAVA_HOME` to a JDK 21 before building.
- Confirm the version under test in `build.gradle.kts` (`version = "..."`).

## Step 2: Compile
```bash
./gradlew compileJava
```

## Step 3: Run tests
```bash
./gradlew test
```
Summarize results from `build/reports/tests/test/index.html` or the console
(`events("passed","skipped","failed")` is enabled).

## Step 4: Install to local `~/.m2` (for consumers)
```bash
./gradlew publishToMavenLocal -I .github/publish.init.gradle.kts
```
The init script injects the `com.vanniktech.maven.publish` plugin — the plain
`build.gradle.kts` carries **no** publishing config by design. For real signing,
export `ORG_GRADLE_PROJECT_signingInMemoryKey` /
`ORG_GRADLE_PROJECT_signingInMemoryKeyPassword` first.

## Step 5: Full check
```bash
./gradlew build    # compile + tests
```

---

## Troubleshooting

| Symptom | Root cause | Fix |
|---|---|---|
| `Cannot perform signing task ... no configured signatory` | GPG env vars not set | Export `ORG_GRADLE_PROJECT_signingInMemoryKey{,Password}`, or skip `-x signMavenPublication` |
| `Extension of type 'JavaPluginExtension' does not exist` in init script | Plugin/config applied before `java-library` | Gate with `pluginManager.withPlugin("java-library") { }` |
| `invalid publication: artifact file does not exist ... .asc` | `-x sign*` used but publication still references signatures | Provide signing env vars instead of skipping |
| `Unchanged`/`UP-TO-DATE` hiding test results | Incremental build | `./gradlew test --rerun` |

## Notes
- Do **not** add publishing plugins/config to `build.gradle.kts` — that lives in
  `.github/publish.init.gradle.kts` + `gradle.properties`.
- Do **not** bump `version` as part of a build — see the `library-release` skill.
