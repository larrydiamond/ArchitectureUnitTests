---
type: reference
title: Rule Catalog
description: All 43 ArchitectureRule enum constants grouped by concern, with what each group matches, how empty matches and because-messages are handled, and the version each group arrived in.
tags: [rules, catalog, archunit, spring, jpa, junit]
verified:
  - by: openwiki/0.8.0
    at: 2026-11-05T03:23:32.912Z
sources:
  - id: openwiki-source-23775c3de52f3ab95a13cb8b
    resource: repo://README.md
  - id: openwiki-source-340a47dea971929d3117d18b
    resource: repo://src/main/java/com/ldiamond/archunittest/ArchitectureRule.java
generated: { by: "claude-code", at: "2026-11-05T03:23:32.912Z" }
---

# Rule Catalog

`ArchitectureRule` declares 43 constants. Each wraps one ArchUnit `ArchRule`. The README's "What architecture tests do we have today?" table lists every constant with a one-line description and the version it was added in ("Since x.y"). This page groups the rules by what they protect against and explains how they match.

## Group summary

| Group | Constants | Matching approach | Since |
|---|---|---|---|
| ArchUnit built-ins | `ARCHUNIT_ASSERTIONS_SHOULD_HAVE_DETAIL_MESSAGE`, `ARCHUNIT_DEPRECATED_API_SHOULD_NOT_BE_USED`, `ARCHUNIT_NO_CLASSES_SHOULD_USE_FIELD_INJECTION`, `ARCHUNIT_NO_CLASSES_SHOULD_USE_JAVA_UTIL_LOGGING`, `ARCHUNIT_NO_CLASSES_SHOULD_THROW_GENERIC_EXCEPTIONS`, `ARCHUNIT_NO_CLASSES_SHOULD_ACCESS_STANDARD_STREAMS`, `ARCHUNIT_NO_CLASSES_SHOULD_USE_JODATIME` | Re-exports of ArchUnit's `GeneralCodingRules` constants | 1.0 |
| Collections & concurrency | `USE_ARRAYLIST_INSTEAD_OF_VECTOR`, `USE_HASHMAP_INSTEAD_OF_HASHTABLE`, `USE_STRINGBUILDER_INSTEAD_OF_STRINGBUFFER`, `USE_CONCURRENTSKIPLISTSET_INSTEAD_OF_TREESET`, `USE_CONCURRENTSKIPLISTMAP_INSTEAD_OF_TREEMAP`, `USE_EXECUTORSERVICE_INSTEAD_OF_THREADGROUP` | `noClasses().should().callConstructor(X.class)` | 1.0 |
| Spring layering (package names) | `SPRING_BOOT_SERVICES_SHOULD_NOT_CALL_CONTROLLERS`, `SPRING_BOOT_REPOSITORIES_SHOULD_NOT_CALL_CONTROLLERS`, `SPRING_BOOT_REPOSITORIES_SHOULD_NOT_CALL_SERVICES` | Classes in `..service..`/`..repository..` must not depend on `..controller..`/`..service..` | 2.0 |
| Spring layering (annotations) | `SPRING_BOOT_SERVICES_SHOULD_NOT_CALL_CONTROLLER_METHODS`, `SPRING_BOOT_REPOSITORIES_SHOULD_NOT_CALL_CONTROLLER_METHODS`, `SPRING_BOOT_REPOSITORIES_SHOULD_NOT_CALL_SERVICE_METHODS` | `@Service`/`@Repository` classes must not depend on `@Controller`/`@Service` classes | 2.0 |
| JPA ↔ REST coupling | `JPA_COUPLING_RESTFUL_{GET,POST,REQUEST}_MAPPINGS` and their `_JAVAX` variants (6) | Methods with `@GetMapping`/`@PostMapping`/`@RequestMapping` must not return a type annotated with `jakarta.persistence.Entity` (or `javax.persistence.Entity`) | 2.0 / 4.0 |
| Spring mapping placement | `SPRING_BOOT_REQUESTMAPPING_CONTROLLER`, `SPRING_BOOT_GETMAPPING_CONTROLLER`, `SPRING_BOOT_POSTMAPPING_CONTROLLER` | Mapping-annotated methods must be declared in a `@RestController` **or** `@Controller` class | 4.0 |
| Spring proxies | `SPRING_CACHEABLE_METHODS_SHOULD_NOT_BE_CALLED_INSIDE_SAME_CLASS`, `SPRING_TRANSACTIONAL_METHODS_SHOULD_NOT_BE_CALLED_INSIDE_SAME_CLASS`, `METHODS_WITH_THE_ASYNC_ANNOTATION_MUST_BE_PUBLIC` | Custom self-invocation condition; `@Async` methods must be public | 5.0 / 5.1 |
| Naming | `INTERFACES_SHOULD_NOT_END_IN_IMPL`, `IS_METHODS_RETURN_PRIMITIVE_BOOLEAN`, `RENAME_IS_FIELDS`, `RESTATE_ISNOT_METHODS_AS_POSITIVE` | Name patterns (`*Impl`, `is[A-Z][a-zA-Z]+`, `isNot[A-Z][a-zA-Z]+`) | 1.0 |
| Optional | `OPTIONAL_NOT_NULLABLE`, `OPTIONAL_NOT_NULLABLE_JAVAX`, `OPTIONAL_NOT_NULLABLE_JSPECIFY` | `@Nullable` (jakarta / javax / JSpecify) methods returning Optional (currently never matches — see Known Issues) | 1.0 |
| JUnit | `JUNIT5_TESTS_CANT_BE_STATIC`, `JUNIT5_TESTS_CANT_BE_PRIVATE` | `@org.junit.jupiter.api.Test` methods must not be static or private | 4.0 / 4.0.2 |
| Library choices | `PREFER_CAFFEINE_OVER_GUAVA_CACHING`, `GUAVA_EVENTBUS_SHOULD_NOT_BE_USED`, `DO_NOT_CONSTRUCT_APACHE_COMMONS_LANG3_TUPLE_CLASSES` | No dependency on `com.google.common.cache`, `com.google.common.eventbus`, `org.apache.commons.lang3.tuple` | 5.0 / 5.1 |

## Group notes

**ArchUnit built-ins.** These simply wrap the matching `GeneralCodingRules` constants, so their behavior and messages come from whatever ArchUnit version is resolved (the build uses `latest.release`). Only the field-injection rule adds `allowEmptyShould(true)`.

**Collections & concurrency.** The `because` text gives the reason: the synchronized legacy classes are slower than their unsynchronized replacements, and the project prefers skip lists over balanced trees. These rules check only constructor calls, not field or parameter types.

**Spring layering.** The two sets overlap on purpose: one works for projects that organize packages by layer, the other for projects that use stereotype annotations. Both sets run unless excluded.

**JPA ↔ REST coupling.** The aim is to stop database table layouts from defining API response shapes; endpoints should return DTOs. Each HTTP mapping comes in two flavours, Jakarta (`jakarta.persistence.Entity`) and legacy javax (`javax.persistence.Entity`). Only `@GetMapping`, `@PostMapping` and `@RequestMapping` are covered; `@PutMapping`, `@DeleteMapping` and `@PatchMapping` are not.

**Spring proxies.** These catch code that compiles but where Spring's proxy-based caching, transactions or async quietly don't apply. See [Custom Conditions and Predicates](custom-conditions.md).

**Naming.** `RESTATE_ISNOT_METHODS_AS_POSITIVE` is written as "no methods matching `isNot…` should have names starting with `isNot`". The condition always holds for the selected methods, so any `isNotX` method is a violation. `RENAME_IS_FIELDS` applies to fields of every type, not just booleans.

**JUnit.** JUnit 5 silently skips static and private `@Test` methods. These rules turn that silent skip into a failure.

## Conventions shared by the rules

- **Empty matches.** Most rules end with `.allowEmptyShould(true)`, so a project with no Spring controllers, no JPA entities and so on passes those rules. The ArchUnit built-ins (except field injection) and the six collection rules leave it off. Those start with `noClasses()` and no filter, so they only "match nothing" if no classes were imported.
- **`because` text.** Newer rules (2.0 onward) start the `because` text with their own enum name, for example `"JPA_COUPLING_RESTFUL_GET_MAPPINGS REST response types …"`. That puts the exact constant to exclude in the failure message. Older 1.0 rules use plain-English reasons only.
- **String-named types.** Framework annotations and packages are referenced by fully qualified strings so the library doesn't need those frameworks at runtime.
- **Enum order is report order.** Violations appear in the combined failure message in declaration order (see [Rule Runner](../architecture/rule-runner.md)).

## Related

- [Known Rule Issues and Gaps](known-issues.md) — defects and untested rules
- [Adding a New Rule](../workflows/adding-a-rule.md)
