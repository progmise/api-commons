---
name: build-and-test
description: Compile, test and locally install this Java/Maven shared library (JDK 21, Maven wrapper, publishing config in pom.xml)
allowed-tools:
  - read
  - exec
  - grep
  - glob
permissions:
  allow:
    - Read(pom.xml)
    - Read(src/**)
  ask:
    - Exec(./mvnw *)
---

# Skill: Build and Test — Java/Maven shared library

## Description
Step-by-step guide to compile, test and install this shared library. It is a
**Maven** project built with the wrapper (`./mvnw`) on Java 21.

## When to Use
- Compiling/testing the library for the first time or on a new machine.
- Diagnosing build/test/dependency-resolution failures.
- Producing a local `~/.m2` install so consumers can pick up an unreleased version.

---

## Step 1: Environment
- **JDK 21**. Set `JAVA_HOME` to a JDK 21 before building.
- Confirm the version under test in `pom.xml` (`<version>...</version>`).

## Step 2: Compile
```bash
./mvnw -B -ntp compile
```

## Step 3: Run tests
```bash
./mvnw -B -ntp test
```
Summarize results from `target/surefire-reports/` or the console.

## Step 4: Install to local `~/.m2` (for consumers)
```bash
./mvnw -B -ntp install
```
Signing lives in the `release` profile (`-Prelease`). For real signing,
import a GPG key first (`gpg --batch --import key.asc`).

## Step 5: Full check
```bash
./mvnw -B -ntp verify    # compile + tests + JaCoCo
```

---

## Troubleshooting

| Symptom | Root cause | Fix |
|---|---|---|
| `gpg: signing failed: No secret key` | GPG key not imported | `gpg --batch --import key.asc`, or omit `-Prelease` |
| `-Prelease` fails asking for credentials | Central/Sonatype creds missing | Set `MAVEN_OPTS`/settings.xml server creds, or use `-DaltDeploymentRepository=local::file:./target/mvn-local` |
| Stale results | Incremental build | `./mvnw -B -ntp clean verify` |

## Notes
- Publishing config lives in `pom.xml`; signing only under the `release` profile.
- Do **not** bump `version` as part of a build — see the `library-release` skill.
