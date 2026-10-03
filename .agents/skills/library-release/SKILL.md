---
name: library-release
description: Coordinated release of this shared library — version bump (patch/minor/major), tag -> GitHub Actions -> Maven Central, and consumer updates
argument-hint: "[change summary]"
allowed-tools:
  - read
  - edit
  - exec
  - grep
  - glob
permissions:
  allow:
    - Read(build.gradle.kts)
    - Read(AGENTS.md)
  ask:
    - Write(build.gradle.kts)
    - Exec(./gradlew *)
    - Exec(git *)
---

# Skill: Library Release

## Description
Drives a **coordinated release** of this shared library. Because consumers depend
on it, a release is never just a version bump — it must respect backward
compatibility and be followed by consumer updates. Publishing is fully automated:
pushing a tag runs `.github/workflows/publish.yml` → `publishToMavenCentral`.

## When to Use
- A change is ready to be published as a new library version.
- You need to make an unreleased version available locally for consumers.

## Preconditions
- Read `AGENTS.md`, especially the backward-compatibility and conventions rules.
- Working tree green (`build-and-test` skill) before bumping.
- Repo secrets configured (`SONATYPE_*`, `GPG_*`) — see README *One-time setup*.

---

## Step 1: Decide the version bump
- **Patch** (`x.y.+1`): bug fixes, dependency patches, docs, small additive helpers.
- **Minor** (`x.+1.0`): new features / auto-configurations / utilities.
- **Major** (`+1.0.0`): breaking changes (requires coordinated upgrade).

If unsure whether the change is breaking, run the `backward-compatibility-check`
skill first. Confirm the target version with the user.

## Step 2: Bump version
Edit `version` in `build.gradle.kts`. In the same change, **update `AGENTS.md`**
version references so the docs stay in sync (docs-as-code).

## Step 3: Build, test, install
`./gradlew build` must be green. Optionally verify the publish pipeline locally:
```bash
./gradlew publishToMavenLocal -I .github/publish.init.gradle.kts
```
This also installs the version into local `~/.m2` for consumer verification.

## Step 4: Commit & tag
```bash
git commit -m "<description>"
git tag <version>          # tag name must equal the version in build.gradle.kts
git push origin main --tags
```

## Step 5: Verify the release
- Check the GitHub Actions run: build → `publishToMavenCentral` → GH Release.
- With `mavenCentralAutomaticPublishing=true` the deployment is released
  automatically — verify on central.sonatype.com / search.maven.org before
  reporting the artifact as published (Central sync can take ~30 min).

## Step 6: Update consumers
For each consumer in `AGENTS.md` -> *Consumers* (confirm local checkout paths
with the user; warn about any not cloned):
- Bump the dependency to the new version.
- Build + test the consumer green.
- Commit and push.

## Deliverables
- New library version + tag + workflow run link.
- `.m2` install confirmation.
- Per-consumer update status (updated / pending / not cloned).

## Rules
- Never bump skipping the strategy (e.g. minor for a plain patch) unless the user
  explicitly asks.
- Never publish a breaking change as patch/minor.
- Keep the *Consumers* list in `AGENTS.md` up to date.
- **This is a library, not a deployable service** — a release only affects the
  published artifact and the consumers' dependency version.
