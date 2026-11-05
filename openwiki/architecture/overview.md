---
type: architecture
title: Architecture Overview
description: How the ArchitectureUnitTests library is structured — a single enum of ArchUnit rules plus a static facade that runs them all against a consumer's packages.
tags: [architecture, archunit, library-design, public-api]
verified:
  - by: openwiki/0.8.0
    at: 2026-11-05T03:23:32.912Z
sources:
  - id: openwiki-source-18fdeaac7d12c7b7f20e3a74
    resource: repo://build.gradle
  - id: openwiki-source-23775c3de52f3ab95a13cb8b
    resource: repo://README.md
  - id: openwiki-source-340a47dea971929d3117d18b
    resource: repo://src/main/java/com/ldiamond/archunittest/ArchitectureRule.java
  - id: openwiki-source-b689dec9efff60172d53fbbb
    resource: repo://src/main/java/com/ldiamond/archunittest/ArchitectureUnitTest.java
generated: { by: "claude-code", at: "2026-11-05T03:23:32.912Z" }
---

# Architecture Overview

ArchitectureUnitTests (Maven coordinates `com.ldiamond:architectureunittests`) is a small Java library that packages an opinionated, "reasonable common sense" set of [ArchUnit](https://www.archunit.org) rules so a project can run all of them with one call from a unit test. The project's stated purpose is to solve ArchUnit's "last mile": ArchUnit provides the rule DSL, and this library chooses which rules to run.

## Components

The whole production surface is two classes in `com.ldiamond.archunittest`:

| Class | Role |
|---|---|
| `ArchitectureRule` (enum) | The rule registry. Each enum constant wraps one ArchUnit `ArchRule`, exposed through `getRule()`. The enum currently declares 43 constants. |
| `ArchitectureUnitTest` (final-by-convention utility class) | The facade. It has a private constructor and two static `testArchitecture` overloads that import classes and check every enum rule. |

There is no configuration file, no service loader, and no runtime state beyond the arguments of a single call. The library is meant to be added as a **test** dependency by consumers (the README recommends `testImplementation`), while the library itself declares `com.tngtech.archunit:archunit-junit5` as an `implementation` dependency so ArchUnit comes along transitively.

## How a consumer uses it

```java
@Test
public void runArchitectureTests() {
    ArchitectureUnitTest.testArchitecture("com.example.myproject");
}
```

1. The consumer passes one or more package names (and optionally a `Collection<ArchitectureRule>` of exclusions).
2. `testArchitecture` imports those packages with ArchUnit's `ClassFileImporter`.
3. Every `ArchitectureRule` constant not in the exclusion collection is checked.
4. All violations are combined into one `AssertionError`, which fails the consumer's JUnit test.

The details of step 3–4 are in [Rule Runner and Failure Aggregation](rule-runner.md).

## Design choices

- **Rules are enum constants.** Because the enum is iterated with `values()`, adding a constant automatically includes the rule in every consumer's run after they upgrade. The README describes this as intentional: projects pick up new rules simply by upgrading the library version.
- **Opt-out, not opt-in.** Every rule is on by default; consumers disable rules by passing the enum constants to exclude. Exclusion is the only customization point.
- **Rules are built at class-load time.** Each constant's `ArchRule` is constructed by ArchUnit's fluent DSL inside the enum constructor argument, so rule definitions are declarative values rather than executable methods. This has consequences for testing — see [Mutation Testing with PIT](../testing/mutation-testing.md).
- **Framework-aware without framework dependencies.** Spring, JPA, Guava and other framework rules reference annotations and packages by fully qualified **string** names (for example `"org.springframework.stereotype.Service"`), so the library does not need Spring or JPA on its classpath. Rules that refer to JDK types (such as `java.util.Vector.class`) use class literals.
- **Empty matches are tolerated.** Most rules call `allowEmptyShould(true)`, so a rule whose "that" clause matches nothing (for example, Spring rules in a non-Spring project) passes instead of failing.

## Where to go next

- [Rule Catalog](../rules/rule-catalog.md) — every rule grouped by concern.
- [Custom Conditions and Predicates](../rules/custom-conditions.md) — the hand-written ArchUnit extensions.
- [Build, CI, and Release](../operations/build-and-release.md) — how the jar is built and published.
