---
name: backward-compatibility-check
description: Verify a change to this shared library keeps the public API backward compatible (self API diff vs the last released version) and recommend the version bump
argument-hint: "[baseline version or git ref; defaults to last released version]"
allowed-tools:
  - read
  - exec
  - grep
  - glob
permissions:
  allow:
    - Read(src/**)
    - Read(build.gradle.kts)
    - Read(AGENTS.md)
  ask:
    - Exec(./gradlew *)
    - Exec(git *)
    - Exec(javap *)
---

# Skill: Backward Compatibility Check

## Description
Checks whether the current state of the library is **backward compatible** with
the last released version, by diffing the **public API of the library itself** —
it does **not** require consumer repositories. Consumers are only relevant for
optional impact analysis.

## When to Use
- Before a release, to confirm the change is non-breaking and pick the right bump.
- Whenever public classes/methods/fields or serialized shapes changed.

## Baseline
Default baseline = the **last released version** (previous git tag / artifact on
Maven Central / previous entry in `~/.m2`). Accept an explicit version or git ref
as `$ARGUMENTS`.

---

## Step 1: Establish the API surface to compare
Prefer, in order of robustness:
1. **japicmp / revapi** if available on the build — compare the current jar
   against the baseline jar.
2. **`javap` signature diff**: `javap -public` over the baseline jar vs the
   freshly built current jar; diff public signatures per class.
3. **`git diff`** of public members between the baseline ref and HEAD (fallback
   when no baseline jar is available).

## Step 2: Flag breaking changes
Treat as **breaking**:
- Removed or renamed public class / method / field.
- Changed public method signature (params/return) — an added overload is OK.
- Renamed a `@ConfigurationProperties` prefix or property key.
- Changed default of a `@ConditionalOnProperty` gate (opt-in must stay opt-in).
- Changed cache key formats, the error JSON shape
  (`{"errors":[{code,message,level,description}]}`), or the `FEATURE_TOGGLE`
  table schema.

Additive changes (new classes/methods, new `default` interface methods, new
opt-in beans) are **compatible**.

## Step 3: Recommend the bump
- Breaking change found -> **major**.
- New feature/utility, no break -> **minor**.
- Fix/dependency/docs/additive helper only -> **patch**.

## Step 4 (optional): Impact analysis
If the user wants usage impact, read *Consumers* from `AGENTS.md`, confirm local
checkout paths, and grep each consumer for usages of the changed/removed symbols.
Warn about consumers not cloned locally.

## Deliverables
- **Verdict**: compatible / breaking.
- **List** of any breaking changes (symbol + kind of break).
- **Recommended version bump** (patch/minor/major) with justification.
- (If requested) impacted consumers and call sites.

## Rules
- Do not modify code in this skill — it is read-only analysis.
- Do not depend on consumer repositories for the core verdict; the API self-diff
  is authoritative.
