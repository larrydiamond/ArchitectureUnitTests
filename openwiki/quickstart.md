---
type: guide
title: Quickstart
description: Orientation for the ArchitectureUnitTests repository — what the library does, how consumers call it, how to build and test it, and which wiki page to read for each task.
tags: [quickstart, overview, navigation]
verified:
  - by: openwiki/0.8.0
    at: 2026-11-05T03:23:32.912Z
sources:
  - id: openwiki-source-18fdeaac7d12c7b7f20e3a74
    resource: repo://build.gradle
  - id: openwiki-source-23775c3de52f3ab95a13cb8b
    resource: repo://README.md
  - id: openwiki-source-b689dec9efff60172d53fbbb
    resource: repo://src/main/java/com/ldiamond/archunittest/ArchitectureUnitTest.java
generated: { by: "claude-code", at: "2026-11-05T03:23:32.912Z" }
---

# Quickstart

**ArchitectureUnitTests** (`com.ldiamond:architectureunittests`, currently version 5.1.0) is a Java 17+ library that bundles 43 opinionated [ArchUnit](https://www.archunit.org) rules. A consumer project adds it as a test dependency and runs every rule with one call:

```java
@Test
public void runArchitectureTests() {
    ArchitectureUnitTest.testArchitecture("com.example.myproject");
}
```

To skip rules that don't fit a project, pass the enum constants to exclude:

```java
ArchitectureUnitTest.testArchitecture(
    Arrays.asList(ArchitectureRule.USE_ARRAYLIST_INSTEAD_OF_VECTOR),
    "com.example.myproject");
```

If any rule is violated, the test fails with one `AssertionError` listing every violation.

## Repository map

| Path | What's there |
|---|---|
| `src/main/java/com/ldiamond/archunittest/ArchitectureRule.java` | The enum holding every rule, plus the two hand-written ArchUnit extensions |
| `src/main/java/com/ldiamond/archunittest/ArchitectureUnitTest.java` | The static `testArchitecture` facade |
| `src/test/java/com/ldiamond/archunittest/TestArchitectureUnitTest.java` | All tests |
| `src/test/java/com/ldiamond/archunittest/<topic>/{good,bad}/…` | Fixture packages that pass or break specific rules |
| `build.gradle` | Build, coverage, PIT, OpenRewrite, Sonar and publishing configuration |
| `.github/workflows/build.yml` | CI: build and SonarCloud analysis |
| `README.md` | User docs and the rule table with "Since" versions |

## Everyday commands

```bash
./gradlew build    # compile, run tests, JaCoCo coverage, jars
./gradlew test     # tests only
./gradlew pitest   # mutation testing -> build/reports/pitest/
```

## Where to read next

| I want to… | Read |
|---|---|
| Understand how the library is put together | [Architecture Overview](architecture/overview.md) |
| Know exactly what happens inside `testArchitecture` | [Rule Runner and Failure Aggregation](architecture/rule-runner.md) |
| Look up what a rule checks | [Rule Catalog](rules/rule-catalog.md) |
| Understand the Spring `@Cacheable`/`@Transactional` self-call rules | [Custom Conditions and Predicates](rules/custom-conditions.md) |
| Know which rules are broken or untested | [Known Rule Issues and Gaps](rules/known-issues.md) |
| Add or change a rule | [Adding a New Rule](workflows/adding-a-rule.md) |
| Understand the fixture-based tests and their exact-length assertions | [Test Suite and Fixture Packages](testing/test-suite.md) |
| Run or interpret mutation testing | [Mutation Testing with PIT](testing/mutation-testing.md) |
| Build, run CI or publish a release | [Build, CI, and Release](operations/build-and-release.md) |

## Things that surprise newcomers

- Rules are **opt-out**: upgrading the library turns on any new rules automatically.
- Each test runs **all** rules, so fixture code must follow every rule except the one it targets.
- The PIT mutation score covers only the runner and two custom extensions, not the declarative rules.
- Release signing is commented out in `build.gradle` and must be re-enabled to publish to Maven Central.
