---
type: testing
title: Test Suite and Fixture Packages
description: How the library tests its rules — each JUnit test runs the full rule set against a small fixture package under src/test, asserting either a clean pass or an exact violation message and length.
tags: [testing, junit5, fixtures, archunit]
verified:
  - by: openwiki/0.8.0
    at: 2026-11-05T03:23:32.912Z
sources:
  - id: openwiki-source-18fdeaac7d12c7b7f20e3a74
    resource: repo://build.gradle
  - id: openwiki-source-2e9f8da6848eaf286c587815
    resource: repo://src/test/java/com/ldiamond/archunittest/TestArchitectureUnitTest.java
  - id: openwiki-source-d2bd8a8137fbf46e65cd113f
    resource: repo://src/test/java/com/ldiamond/archunittest/testpackage/ClassWithViolationsUnderTest.java
generated: { by: "claude-code", at: "2026-11-05T03:23:32.912Z" }
---

# Test Suite and Fixture Packages

All tests live in one class, `src/test/java/com/ldiamond/archunittest/TestArchitectureUnitTest.java`, which has 34 `@Test` methods. The tests exercise the library the way a consumer would: through `ArchitectureUnitTest.testArchitecture(...)`, never by calling `ArchitectureRule.X.getRule().check(...)` directly.

## Fixture packages

Next to the test class are small **fixture packages**. Each one contains just enough code to trigger, or deliberately avoid, a particular rule. Fixtures are compiled as ordinary test sources, which is why Spring, JPA, Guava and commons-lang3 are test-scoped dependencies in `build.gradle`.

Fixtures usually come in pairs named by intent:

| Pattern | Examples |
|---|---|
| `<topic>.bad` / `<topic>.good` | `springasync`, `springcache`, `springtransactional`, `springannotations`, `commonslang3tuple`, `primitiveBooleanIs` |
| descriptive names | `jparest.hasProblem` / `jparest.hasNoProblem`, `restatePositive.hasNegative` / `hasPositive`, `IsReturnsBoolean.IsDoesNotReturnBoolean` / `IsDoesReturnBoolean` |
| one package per scenario | `impl.hasImplInterface`, `impl.doesNotHaveImplInterface`, `impl.doesNotHaveInterfaces`; `springdependencies.allgood`, `servicedependsoncontroller`, `repositorydependsoncontroller`, `repositorydependsonservice` |
| general violations | `testpackage.ClassWithViolationsUnderTest` creates a `Vector` and a `Hashtable` |
| JUnit fixtures | `invalidjunittests` (a static and a private `@Test`) and `validjunittests` |

`ClassFileImporter.importPackages` also imports **subpackages**. That's why tests always point at the leaf package (`springcache.bad`, never `springcache`), and no test imports the root `com.ldiamond.archunittest` package, which would pull in every fixture at once.

## Two kinds of tests

**Positive tests** call `testArchitecture("…good")` and pass as long as no exception is thrown. Because *every* rule runs, each positive test also shows that none of the 43 rules gives a false positive on that fixture.

**Negative tests** wrap the call in `assertThrowsExactly(AssertionError.class, …)` and then usually check three things:

1. `contains(...)` on the full rule header, for example
   `Architecture Violation [Priority: MEDIUM] - Rule '…, because …' was violated (1 times):`
2. `contains(...)` on the specific violation line, including the fixture's source file and line number, for example `(ClassWithViolationsUnderTest.java:26)`
3. the **exact length** of `ae.toString()`, accepting two values (such as `case 865, 867`) to allow for LF vs CRLF line endings

The length check is what pins the result down: it fails if any *other* rule also fires on the fixture, or if a message changes at all. The newest tests (`springasync`, `commonslang3tuple`) leave the length check out and assert only on `contains`.

## Exclusion tests

Three tests use `testpackage` to cover the exclusion API:

- no exclusions → both the Vector and Hashtable violations
- exclude `USE_ARRAYLIST_INSTEAD_OF_VECTOR` → only the Hashtable violation, with a shorter expected length
- exclude both → passes

## Invariants and gotchas

- **A fixture must break only the rules it targets.** Fixture code also has to follow every other rule. For example, it can't print to `System.out`, throw a generic `Exception`, or name a field `isX`, or else the exact-length assertions fail. When you add a rule, run the whole suite: the new rule may fire on existing fixtures.
- **Messages and line numbers are part of the contract.** Moving a line in a `bad` fixture, upgrading ArchUnit (the build uses `latest.release`), or editing a `because` string all change the asserted text and lengths.
- **Test comments name the rule being tested** (`// negative test for X`). Some comments are wrong. The `springannotations` tests say `SPRING_BOOT_REQUESTMAPPING_CONTROLLER` but actually exercise `SPRING_BOOT_GETMAPPING_CONTROLLER`.
- **Coverage is uneven.** 24 rules have no violating fixture; the list is in [Known Rule Issues and Gaps](../rules/known-issues.md#rules-with-no-test-proving-they-fire).

## Running

```bash
./gradlew test       # also writes a JaCoCo report to build/jacocoHtml
./gradlew pitest     # mutation testing, see the Mutation Testing page
```

## Related

- [Mutation Testing with PIT](mutation-testing.md)
- [Adding a New Rule](../workflows/adding-a-rule.md)
