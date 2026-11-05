---
type: reference
title: Known Rule Issues and Gaps
description: Verified defects and surprises in the current ArchitectureRule definitions — Optional rules that can never match, copy-pasted because messages, Spring rules based on package names, tuple rule scope — and the rules that no test exercises.
tags: [rules, defects, test-gaps, maintenance]
verified:
  - by: openwiki/0.8.0
    at: 2026-11-05T03:23:32.912Z
sources:
  - id: openwiki-source-340a47dea971929d3117d18b
    resource: repo://src/main/java/com/ldiamond/archunittest/ArchitectureRule.java
  - id: openwiki-source-888108b3534172ea04ea29a3
    resource: repo://src/test/java/com/ldiamond/archunittest/springannotations/bad/BadService.java
  - id: openwiki-source-2e9f8da6848eaf286c587815
    resource: repo://src/test/java/com/ldiamond/archunittest/TestArchitectureUnitTest.java
generated: { by: "claude-code", at: "2026-11-05T03:23:32.912Z" }
---

# Known Rule Issues and Gaps

This page records problems found by reading the current rule definitions and tests. Each item names the rule and what goes wrong, so a fix can be judged on its own. None of these are fixed yet.

## Defects

### `OPTIONAL_NOT_NULLABLE*` can never report a violation

The three Optional rules (`OPTIONAL_NOT_NULLABLE`, `_JAVAX`, `_JSPECIFY`) are written as:

```java
noMethods().that().areAnnotatedWith("jakarta.annotation.Nullable")
    .should().haveRawReturnType(Predicates.annotatedWith("java.util.Optional"))
```

`Predicates.annotatedWith("java.util.Optional")` checks whether the return **type** carries an *annotation* named `java.util.Optional`. `Optional` is a class, not an annotation, so the predicate is always false. The rules always pass, and a `@Nullable Optional<Foo> find()` method goes unreported. The intended check is something like `.should().haveRawReturnType(java.util.Optional.class)`. No fixture exercises these rules, so nothing catches this.

### Copy-pasted `because` messages

| Rule | Message says | Should say |
|---|---|---|
| `SPRING_BOOT_POSTMAPPING_CONTROLLER` | "SPRING_BOOT_POSTMAPPING_CONTROLLER **GetMapping** methods should be in Controller classes" | PostMapping |
| `JPA_COUPLING_RESTFUL_POST_MAPPINGS_JAVAX` | "JPA_COUPLING_RESTFUL_**GET**_MAPPINGS_JAVAX REST response types…" | POST |

The rule logic is correct. Only the text users see when a rule fails is wrong, which sends them to the wrong rule name when they try to exclude it.

## Surprises (behavior that may not match the name)

### Two sets of Spring layering rules

There are two parallel sets of Spring layering rules:

| Based on package names | Based on annotations |
|---|---|
| `SPRING_BOOT_SERVICES_SHOULD_NOT_CALL_CONTROLLERS` | `SPRING_BOOT_SERVICES_SHOULD_NOT_CALL_CONTROLLER_METHODS` |
| `SPRING_BOOT_REPOSITORIES_SHOULD_NOT_CALL_CONTROLLERS` | `SPRING_BOOT_REPOSITORIES_SHOULD_NOT_CALL_CONTROLLER_METHODS` |
| `SPRING_BOOT_REPOSITORIES_SHOULD_NOT_CALL_SERVICES` | `SPRING_BOOT_REPOSITORIES_SHOULD_NOT_CALL_SERVICE_METHODS` |

The package-based rules use ArchUnit package patterns like `..service..` and `..controller..`, which match only a package **segment** with exactly that name (`com.acme.service.x`), not `services` or `servicedependsoncontroller`. The annotation-based rules match `@Service`, `@Repository` and `@Controller` (note: not `@RestController`). A project can trip one set without the other.

### `DO_NOT_CONSTRUCT_APACHE_COMMONS_LANG3_TUPLE_CLASSES` bans any use

Despite "construct" in the name, the rule is `noClasses().should().dependOnClassesThat().resideInAPackage("org.apache.commons.lang3.tuple")`. Any dependency counts: a field type, a parameter, a return type, a static factory call such as `Pair.of(...)`. It isn't limited to constructor calls.

### `SPRING_BOOT_*MAPPING_CONTROLLER` doesn't accept `@RestController` meta-annotations

These rules accept methods declared in classes annotated **directly** with `@RestController` or `@Controller`. A custom stereotype annotation that is itself meta-annotated with `@Controller` isn't accepted, because the rules use `areAnnotatedWith`, not `areMetaAnnotatedWith`.

### Collection rules only look at constructor calls

`USE_ARRAYLIST_INSTEAD_OF_VECTOR`, `USE_HASHMAP_INSTEAD_OF_HASHTABLE`, `USE_STRINGBUILDER_INSTEAD_OF_STRINGBUFFER`, the `TreeSet`/`TreeMap` rules and `USE_EXECUTORSERVICE_INSTEAD_OF_THREADGROUP` use `callConstructor(X.class)`. Declaring a `Vector` parameter or receiving one from a library isn't flagged; only `new Vector<>()` in the imported code is.

## Rules with no test proving they fire

Each test runs **all** rules against a fixture package (see [Test Suite and Fixture Packages](../testing/test-suite.md)), so every rule gets checked for false positives against every fixture. But only rules with a fixture that violates them are proven to *detect* anything. These 24 of the 43 rules have no such fixture:

- ArchUnit built-ins (7): `ARCHUNIT_ASSERTIONS_SHOULD_HAVE_DETAIL_MESSAGE`, `ARCHUNIT_DEPRECATED_API_SHOULD_NOT_BE_USED`, `ARCHUNIT_NO_CLASSES_SHOULD_USE_FIELD_INJECTION`, `ARCHUNIT_NO_CLASSES_SHOULD_USE_JAVA_UTIL_LOGGING`, `ARCHUNIT_NO_CLASSES_SHOULD_THROW_GENERIC_EXCEPTIONS`, `ARCHUNIT_NO_CLASSES_SHOULD_ACCESS_STANDARD_STREAMS`, `ARCHUNIT_NO_CLASSES_SHOULD_USE_JODATIME`
- Collections/concurrency (4): `USE_STRINGBUILDER_INSTEAD_OF_STRINGBUFFER`, `USE_CONCURRENTSKIPLISTSET_INSTEAD_OF_TREESET`, `USE_CONCURRENTSKIPLISTMAP_INSTEAD_OF_TREEMAP`, `USE_EXECUTORSERVICE_INSTEAD_OF_THREADGROUP`
- Spring rules based on package names (3): all three listed above. The `springdependencies.*` fixtures only trigger the annotation-based versions.
- JPA/REST coupling (5): `JPA_COUPLING_RESTFUL_GET_MAPPINGS_JAVAX`, `JPA_COUPLING_RESTFUL_POST_MAPPINGS`, `JPA_COUPLING_RESTFUL_POST_MAPPINGS_JAVAX`, `JPA_COUPLING_RESTFUL_REQUEST_MAPPINGS`, `JPA_COUPLING_RESTFUL_REQUEST_MAPPINGS_JAVAX`
- Spring mapping placement (2): `SPRING_BOOT_REQUESTMAPPING_CONTROLLER` and `SPRING_BOOT_POSTMAPPING_CONTROLLER`. The tests whose comments mention `SPRING_BOOT_REQUESTMAPPING_CONTROLLER` actually use `@GetMapping` fixtures and assert the `SPRING_BOOT_GETMAPPING_CONTROLLER` message.
- Optional (3): all three `OPTIONAL_NOT_NULLABLE*` rules, which is why the defect above went unnoticed.

Mutation testing can't detect these gaps automatically; see [Mutation Testing with PIT](../testing/mutation-testing.md).

## Related

- [Rule Catalog](rule-catalog.md)
- [Adding a New Rule](../workflows/adding-a-rule.md)
