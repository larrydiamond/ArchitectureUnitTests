---
type: workflow
title: Adding a New Rule
description: Step-by-step workflow for contributing a new architecture rule — enum constant, because message, good/bad fixture packages, positive and negative tests, README row, and dependency/version updates.
tags: [workflow, contributing, rules, testing]
verified:
  - by: openwiki/0.8.0
    at: 2026-11-05T03:23:32.912Z
sources:
  - id: openwiki-source-23775c3de52f3ab95a13cb8b
    resource: repo://README.md
  - id: openwiki-source-340a47dea971929d3117d18b
    resource: repo://src/main/java/com/ldiamond/archunittest/ArchitectureRule.java
  - id: openwiki-source-b689dec9efff60172d53fbbb
    resource: repo://src/main/java/com/ldiamond/archunittest/ArchitectureUnitTest.java
  - id: openwiki-source-45eba8eb3e6ce8ae2a83d897
    resource: repo://src/test/java/com/ldiamond/archunittest/commonslang3tuple/bad/BadTupleUsage.java
  - id: openwiki-source-2f528cb775cde40f176ed435
    resource: repo://src/test/java/com/ldiamond/archunittest/springasync/bad/BadAsync.java
  - id: openwiki-source-2e9f8da6848eaf286c587815
    resource: repo://src/test/java/com/ldiamond/archunittest/TestArchitectureUnitTest.java
generated: { by: "claude-code", at: "2026-11-05T03:23:32.912Z" }
---

# Adding a New Rule

New rules are this project's main extension point, and the README actively asks for them ("**WE WANT YOUR GREAT ArchRules**"). Recent rule commits (for example the `@Async` and commons-lang3 tuple rules) follow the same pattern. A typical rule touches `ArchitectureRule.java`, `TestArchitectureUnitTest.java`, two fixture classes, `README.md`, and sometimes `build.gradle`.

## 1. Add the enum constant

Add a constant to `src/main/java/com/ldiamond/archunittest/ArchitectureRule.java`, before the terminating `;`. When appending at the end, move the `;` so the previous last constant now ends with `,`.

```java
/**
 * One-line description of what the rule prevents and why.
 */
MY_NEW_RULE (noClasses().should().dependOnClassesThat().resideInAPackage("com.example.bad")
    .allowEmptyShould(true)
    .because("MY_NEW_RULE Explanation users will read when the rule fails")),
```

Conventions:

- **Name** — `UPPER_SNAKE_CASE`, describing the desired state (`…_SHOULD_NOT_…`, `PREFER_…`, `USE_X_INSTEAD_OF_Y`). Once released, the name is public API: consumers type it to exclude the rule.
- **`because` text** — start with the constant's own name so the failure message tells users exactly what to exclude. Copy-pasting from a neighboring rule has caused wrong messages before (see [Known Rule Issues](../rules/known-issues.md#copy-pasted-because-messages)).
- **`allowEmptyShould(true)`** — add it whenever the rule starts with a filter (`that()…`) that may match nothing in a given project, such as framework-specific rules.
- **Framework types by string** — refer to Spring, JPA or other optional framework annotations and packages by fully qualified string, not class literal, so the library doesn't need those frameworks at runtime.
- **Order** — new constants usually go at the end. Enum order decides where violations appear in the combined failure message.
- **Custom logic** — if the DSL can't express the rule, add a static factory that returns an `ArchCondition` or `DescribedPredicate`, as `beCalledByAnyMethodInTheSameClass()` does (see [Custom Conditions](../rules/custom-conditions.md)).

The rule is picked up automatically: `testArchitecture` iterates `ArchitectureRule.values()`, so there is no registry to update.

## 2. Create fixture packages

Under `src/test/java/com/ldiamond/archunittest/<topic>/`, add:

- `bad/` — the smallest class that violates the new rule (exactly once, to keep assertions simple)
- `good/` — the recommended alternative, which must pass **every** rule

Both fixtures must follow all the *other* rules too. No `System.out`, generic exceptions, `isX` fields, `Vector` and so on, or the exact-message assertions break. If a fixture needs a framework, add it to `build.gradle` as `testImplementation` or `testCompileOnly`, never `implementation`.

## 3. Add tests

In `TestArchitectureUnitTest.java`, add a pair:

```java
@Test void testMyNewRuleFailsRuleset() { // negative test for MY_NEW_RULE
    AssertionError ae = assertThrowsExactly(AssertionError.class, () ->
        ArchitectureUnitTest.testArchitecture("com.ldiamond.archunittest.<topic>.bad"));
    assertTrue(ae.toString().contains("… because MY_NEW_RULE … was violated (1 times):"));
}

@Test void testMyNewRuleDoesNotFailRuleset() { // positive test for MY_NEW_RULE
    ArchitectureUnitTest.testArchitecture("com.ldiamond.archunittest.<topic>.good");
}
```

Older tests also assert the exact `ae.toString().length()` with two accepted values (LF and CRLF). That's the strongest check, since it also proves no other rule fired. Run the test once and read the actual length from the failure message `"Actual length: …"`.

Then run the **whole** suite: a new rule can fire on existing fixtures and change their expected lengths.

```bash
./gradlew test
./gradlew pitest   # only useful if you added procedural logic (custom conditions/predicates)
```

## 4. Document and version

- Add a row to the README table "What architecture tests do we have today?" with the constant name, a description and `(Since x.y.z)`.
- If this is a release, bump `version` in `build.gradle` and the version shown in the README's Maven/Gradle snippets. See [Build, CI, and Release](../operations/build-and-release.md) for publishing.
- New rules are on by default for every consumer who upgrades, so mention them in the release notes. Consumers whose code violates a new rule will see their architecture test fail until they fix the code or exclude the rule.

## Related

- [Rule Catalog](../rules/rule-catalog.md)
- [Test Suite and Fixture Packages](../testing/test-suite.md)
