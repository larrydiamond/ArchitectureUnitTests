---
type: operations
title: Build, CI, and Release
description: How the library is built with Gradle (Java 17 target), analyzed in GitHub Actions with SonarCloud, kept up to date with OpenRewrite and dependency plugins, and published to Maven Central.
tags: [build, gradle, ci, sonarcloud, release, maven-central]
verified:
  - by: openwiki/0.8.0
    at: 2026-11-05T03:23:32.912Z
sources:
  - id: openwiki-source-7a80b79a6fb3618cbfab08a2
    resource: repo://.github/workflows/build.yml
  - id: openwiki-source-18fdeaac7d12c7b7f20e3a74
    resource: repo://build.gradle
generated: { by: "claude-code", at: "2026-11-05T03:23:32.912Z" }
---

# Build, CI, and Release

## Build system

The project builds with **Gradle**, using the checked-in wrapper (`./gradlew`). There is no `settings.gradle`; the project name comes from the directory. A `.mvn/wrapper` file and a `build.gradle.backup` (an old 0.2.0-era build file) are leftovers and aren't used by the build.

Key settings in `build.gradle`:

| Setting | Value / effect |
|---|---|
| `group` / `version` | `com.ldiamond` / `5.1.0` (keep in sync with the README's dependency snippets) |
| Java | `sourceCompatibility` and `targetCompatibility` = 17, so the jar runs on Java 17+ even when built with a newer JDK |
| Main dependency | `com.tngtech.archunit:archunit-junit5:latest.release` (`implementation`) — the version floats to ArchUnit's newest release on each build |
| Test dependencies | JUnit Jupiter 6.x, plus **compile-only** Spring (context, web, tx), Jakarta Persistence, Guava and commons-lang3, needed only so fixture classes under `src/test` compile |
| Repositories | Maven Central plus Spring milestone and snapshot repositories |
| Tests | `useJUnitPlatform()`; `test` is `finalizedBy jacocoTestReport` |
| Coverage | JaCoCo writes XML, CSV, and HTML (HTML to `build/jacocoHtml`) |
| Artifacts | `withJavadocJar()` and `withSourcesJar()` produce the extra jars Maven Central requires |

Common commands:

```bash
./gradlew build          # compile, test, JaCoCo report, jars
./gradlew pitest         # mutation testing (see the Mutation Testing page)
./gradlew rewriteRun     # apply the configured OpenRewrite recipes
./gradlew dependencyUpdates  # ben-manes report of newer dependency versions
./gradlew useLatestVersions  # rewrite build.gradle to the latest versions
./gradlew dependencyCheckAnalyze  # OWASP dependency vulnerability scan
```

## Code-maintenance plugins

- **OpenRewrite** (`org.openrewrite.rewrite`, pinned to 7.39.0 because newer versions depend on an unpublished `rewrite-bom`). The `rewrite { }` block turns on a curated list of recipes: ArchUnit 0→1 migration, JUnit assertion cleanups, static-analysis fixes, Guava-to-JDK migrations, and `java.util` modernizations. Recipes only run when someone runs `rewriteRun`; they aren't part of `build`.
- **Dependency freshness**: `se.patrikerdes.use-latest-versions` and `io.github.ben-manes.versions`.
- **Security**: `org.owasp.dependencycheck`.
- **Mutation testing**: `info.solidsoft.pitest` — see [Mutation Testing with PIT](../testing/mutation-testing.md).

## Continuous integration

`.github/workflows/build.yml` (named "SonarQube") runs on pushes to `main` and on pull request open/sync/reopen:

1. Check out with full history (`fetch-depth: 0`), which Sonar's new-code analysis uses.
2. Set up Zulu JDK 17.
3. Cache `~/.sonar/cache` and `~/.gradle/caches`.
4. Run `./gradlew build sonar --info` with the `SONAR_TOKEN` secret.

The `sonar { }` block targets project `larrydiamond_ArchitectureUnitTests` in organization `larrydiamond-github` with SCM integration disabled. The quality, coverage, and bug badges in the README come from that SonarCloud project. CI doesn't run PIT, OpenRewrite, or the OWASP check.

## Publishing

`maven-publish` defines a `mavenJava` publication with artifactId `architectureunittests` and full POM metadata (Apache 2.0 license, developer, SCM URLs). There are two repositories:

- `myRepo` — a local directory at `build/repo`, useful for checking the published layout.
- `OSSRH` — Sonatype `s01`. Versions ending in `SNAPSHOT` go to the snapshots repository; anything else goes to release staging. Credentials come from the Gradle properties `ossrhUsername`/`ossrhPassword` or the environment variables `OSSRH_USERNAME`/`OSSRH_PASSWORD`.

**Signing is commented out.** The `signing { ... }` block and the `dependsOn sign...` wiring at the end of `build.gradle` sit inside a block comment marked "only needed when building release version". To cut a release you un-comment it, which uses `useGpgCmd()`. Publishing to Maven Central without it fails, because Central requires signatures.

## Related

- [Mutation Testing with PIT](../testing/mutation-testing.md)
- [Adding a New Rule](../workflows/adding-a-rule.md) — includes the version/README bump steps
