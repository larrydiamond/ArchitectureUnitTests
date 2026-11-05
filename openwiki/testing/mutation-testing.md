---
type: testing
title: Mutation Testing with PIT
description: How PIT mutation testing is configured in the Gradle build, how to run it and read its reports, and why its score covers only the small amount of procedural code in this library.
tags: [testing, mutation-testing, pitest, gradle]
verified:
  - by: openwiki/0.8.0
    at: 2026-11-05T03:23:32.912Z
sources:
  - id: openwiki-source-7a80b79a6fb3618cbfab08a2
    resource: repo://.github/workflows/build.yml
  - id: openwiki-source-18fdeaac7d12c7b7f20e3a74
    resource: repo://build.gradle
  - id: openwiki-source-340a47dea971929d3117d18b
    resource: repo://src/main/java/com/ldiamond/archunittest/ArchitectureRule.java
  - id: openwiki-source-b689dec9efff60172d53fbbb
    resource: repo://src/main/java/com/ldiamond/archunittest/ArchitectureUnitTest.java
generated: { by: "claude-code", at: "2026-11-05T03:23:32.912Z" }
---

# Mutation Testing with PIT

Mutation testing makes small changes ("mutants") to the compiled production code, such as flipping a condition, removing a call or returning `null`, and reruns the tests. A mutant the tests catch is **killed**; one they don't is a **survivor**. Survivors point to behavior no test checks.

## Configuration

The build applies the `info.solidsoft.pitest` Gradle plugin. Its `pitest { }` block in `build.gradle` sets:

| Setting | Value | Effect |
|---|---|---|
| `pitestVersion` | `1.30.1` | PIT engine version |
| `junit5PluginVersion` | `1.2.3` | Lets PIT run the JUnit Jupiter tests |
| `targetClasses` | `com.ldiamond.archunittest.*` | Mutates both production classes |
| `targetTests` | `com.ldiamond.archunittest.*` | Uses `TestArchitectureUnitTest` |
| `threads` | `Runtime.runtime.availableProcessors()` | One worker per CPU core |
| `outputFormats` | `HTML`, `XML` | Human report plus `mutations.xml` for tooling |
| `timestampedReports` | `false` | Reports go to the same folder on every run |
| `exportLineCoverage` | `true` | Also writes `linecoverage.xml` |

PIT isn't part of `build` and doesn't run in CI (see [Build, CI, and Release](../operations/build-and-release.md)).

## Running it

```bash
./gradlew pitest
```

A full run takes roughly ten seconds. Reports are written to `build/reports/pitest/`:

- `index.html` — per-class summary with drill-down to source lines marked killed or survived
- `mutations.xml` — one `<mutation>` element per mutant with `status`, `mutatedMethod`, `lineNumber`, `mutator` and `killingTest`

## What the score does and doesn't measure

PIT can only mutate code that runs as ordinary methods after the class has loaded. In this library that means:

- `ArchitectureUnitTest.testArchitecture`: the exclusion check, the `check` call, the empty-failures branch and the message building
- the anonymous predicate in `IS_METHODS_RETURN_PRIMITIVE_BOOLEAN`
- the `check` method of `beCalledByAnyMethodInTheSameClass()`'s condition
- `ArchitectureRule.getRule()`

The other ~40 rules are built entirely in enum constant initializers with ArchUnit's DSL. That code runs once, while the class loads, before PIT injects mutants, so PIT generates **no** mutants for the rule definitions themselves. Expect a run to produce only about a dozen mutants. A high score says the runner and the two custom extensions are well tested. It says nothing about whether each declarative rule is proven to fire. That gap is tracked in [Known Rule Issues and Gaps](../rules/known-issues.md#rules-with-no-test-proving-they-fire).

### The expected survivor

The mutant that makes `beCalledByAnyMethodInTheSameClass()` return `null` survives. That factory is only called from enum constant initializers, which run once per JVM while the class loads, so the mutated version never takes effect during a test. It can't be killed. To keep it out of the score, add `excludedMethods = ['beCalledByAnyMethodInTheSameClass']` to the `pitest` block.

## Testing declarative rules

The rule-level equivalent of a mutation test is a fixture package that violates the rule, plus a test asserting the rule's message. Breaking or deleting the rule then fails that test. See [Test Suite and Fixture Packages](test-suite.md) and [Adding a New Rule](../workflows/adding-a-rule.md).

## Related

- [Custom Conditions and Predicates](../rules/custom-conditions.md)
- [Test Suite and Fixture Packages](test-suite.md)
