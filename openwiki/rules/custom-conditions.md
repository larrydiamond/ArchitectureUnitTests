---
type: concept
title: Custom Conditions and Predicates
description: The two hand-written ArchUnit extensions in ArchitectureRule — the same-class self-invocation condition behind the Spring @Cacheable/@Transactional rules, and the primitive-boolean return-type predicate.
tags: [rules, archunit, spring, proxies, custom-condition]
verified:
  - by: openwiki/0.8.0
    at: 2026-11-05T03:23:32.912Z
sources:
  - id: openwiki-source-340a47dea971929d3117d18b
    resource: repo://src/main/java/com/ldiamond/archunittest/ArchitectureRule.java
  - id: openwiki-source-080b7c6a5f0b0bedf65d23ae
    resource: repo://src/test/java/com/ldiamond/archunittest/IsReturnsBoolean/IsDoesNotReturnBoolean/Blah.java
  - id: openwiki-source-83e80d41871a60aded4a4299
    resource: repo://src/test/java/com/ldiamond/archunittest/springcache/bad/BadCache.java
  - id: openwiki-source-e50ef1143915d268e5389eed
    resource: repo://src/test/java/com/ldiamond/archunittest/springcache/good/GoodCacheCaller.java
  - id: openwiki-source-2e9f8da6848eaf286c587815
    resource: repo://src/test/java/com/ldiamond/archunittest/TestArchitectureUnitTest.java
generated: { by: "claude-code", at: "2026-11-05T03:23:32.912Z" }
---

# Custom Conditions and Predicates

Almost every rule in `ArchitectureRule` is built only from ArchUnit's fluent DSL. Two rules need logic that the DSL doesn't offer, and those are the only hand-written code in the rule set.

## `beCalledByAnyMethodInTheSameClass()` — self-invocation condition

### Why it exists

Spring adds caching (`@Cacheable`), transactions (`@Transactional`) and async execution by wrapping a bean in a **proxy**. The extra behavior only runs when a call goes *through* the proxy. When a method calls another method of its own class (`this.findAuthorById(id)`), the call never reaches the proxy, so the cache is skipped or no new transaction starts. The code compiles and runs, but quietly does the wrong thing.

### How it works

`beCalledByAnyMethodInTheSameClass()` is a package-private static factory at the bottom of the enum. It returns an anonymous `ArchCondition<JavaMethod>` described as "be called by any method in the same class":

1. For the method under test, it walks `javaMethod.getCallsOfSelf()`, meaning every call site, among the imported classes, that targets this method.
2. For each call it compares the fully qualified names of the call's **origin owner** and **target owner**.
3. If they match, it adds a `SimpleConditionEvent` with `satisfied = true` and the message
   `Method <target> is called by <caller> in the same class <owner>`.

The rules use it in negated form:

```java
SPRING_CACHEABLE_METHODS_SHOULD_NOT_BE_CALLED_INSIDE_SAME_CLASS(
    noMethods().that().areAnnotatedWith("org.springframework.cache.annotation.Cacheable")
        .should(beCalledByAnyMethodInTheSameClass()) ...)

SPRING_TRANSACTIONAL_METHODS_SHOULD_NOT_BE_CALLED_INSIDE_SAME_CLASS(
    noMethods().that().areAnnotatedWith("org.springframework.transaction.annotation.Transactional")
        .should(beCalledByAnyMethodInTheSameClass()) ...)
```

Because of `noMethods()`, a *satisfied* event (a same-class call was found) is reported as a violation. If no same-class call is found, no event is produced and the method passes.

### Boundaries

- The comparison uses class names, so a call from a **nested or inner class** (`Outer$Inner`) counts as a different class and isn't reported, even though it also skips the proxy.
- A recursive call (a method calling itself) has the same origin and target owner, so it **is** reported.
- Only calls among the imported classes are seen. A call from a class outside the packages passed to `testArchitecture` can't be detected.
- The condition only looks at the annotation on the method. A class-level `@Transactional` doesn't select the class's methods.
- `METHODS_WITH_THE_ASYNC_ANNOTATION_MUST_BE_PUBLIC` covers a related proxy problem (non-public `@Async` methods) with the built-in `bePublic()` check and doesn't use this condition.

### Tests

| Fixture package | Expectation |
|---|---|
| `springcache.bad` — `BadCache.findAnotherAuthorById` calls the `@Cacheable` `findAuthorById` on itself | fails, 1 violation |
| `springcache.good` — `GoodCacheCaller` calls `GoodCacheUsage.findAuthorById` from another class | passes |
| `springtransactional.bad` — `saveAnotherAuthor` calls the `@Transactional` `saveAuthor` | fails, 1 violation |
| `springtransactional.good` | passes |

The negative tests assert the exact "is called by … in the same class …" message.

## Primitive-boolean predicate

`IS_METHODS_RETURN_PRIMITIVE_BOOLEAN` selects methods whose names match `is[A-Z][a-zA-Z]+` and requires `haveRawReturnType(...)` with an inline `DescribedPredicate<JavaClass>` named "primitive boolean":

```java
return input.isPrimitive() && "boolean".equals(input.getName());
```

Both parts are needed. `isPrimitive()` rejects the boxed `java.lang.Boolean`, which can return `null`. The name check rejects other primitives such as `int isCount()`.

Tests: `IsReturnsBoolean.IsDoesNotReturnBoolean.Blah.isBad()` returns `Boolean` and fails. `IsReturnsBoolean.IsDoesReturnBoolean.Blah.isGood()` returns `boolean` and passes. No fixture currently covers a non-boolean primitive.

## Related

- [Rule Catalog](rule-catalog.md)
- [Mutation Testing with PIT](../testing/mutation-testing.md) — these two extensions are the only rule logic PIT can mutate
