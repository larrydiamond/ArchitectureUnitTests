---
type: architecture
title: Rule Runner and Failure Aggregation
description: Control flow of ArchitectureUnitTest.testArchitecture — importing classes, skipping excluded rules, checking each rule, and combining every violation into one AssertionError.
tags: [architecture, runner, error-handling, exclusions]
verified:
  - by: openwiki/0.8.0
    at: 2026-11-05T03:23:32.912Z
sources:
  - id: openwiki-source-b689dec9efff60172d53fbbb
    resource: repo://src/main/java/com/ldiamond/archunittest/ArchitectureUnitTest.java
generated: { by: "claude-code", at: "2026-11-05T03:23:32.912Z" }
---

# Rule Runner and Failure Aggregation

`ArchitectureUnitTest.testArchitecture` is the library's only entrypoint. It runs every enabled rule against the classes a consumer names, then reports all violations together instead of stopping at the first failing rule.

## Entry points

```java
public static void testArchitecture(Collection<ArchitectureRule> exclusions, String... packagesToImport)
public static void testArchitecture(String... packagesToImport)
```

The single-argument overload simply calls the first with `new ArrayList<>()`, meaning "no exclusions".

## Control flow

1. **Import.** `new ClassFileImporter().importPackages(packagesToImport)` loads the bytecode of every class in the named packages and their subpackages into a `JavaClasses` snapshot. No import options are set, so ArchUnit's defaults apply — test classes and jars under those package names are included if they're on the classpath.
2. **Iterate rules.** The method loops over `ArchitectureRule.values()` in declaration order.
3. **Filter.** A rule is skipped if `exclusions.contains(rule)` is true. Because `contains` is used, any `Collection` works; an `EnumSet` gives the fastest lookup, but the README examples use `Arrays.asList(...)`.
4. **Check.** For each remaining rule, `rule.getRule().check(classes)` runs. ArchUnit signals a violation by throwing `AssertionError`.
5. **Collect.** Each `AssertionError` is caught and added to a `failures` list, so later rules still run.
6. **Report.** If `failures` is empty the method returns normally (the consumer's test passes). Otherwise it builds one message:

   ```
   The following architecture rules were violated:
   <message of failure 1>

   <message of failure 2>

   ...
   ```

   Each ArchUnit message is followed by two newlines, and a new `AssertionError` with that text is thrown.

## Behavior to know about

- **Only `AssertionError` is aggregated.** Any other exception thrown while checking a rule (for example an ArchUnit configuration error) is not caught and ends the run immediately.
- **The original errors are not chained.** The combined error carries only the concatenated text; the individual `AssertionError` objects and their stack traces are dropped.
- **Order is deterministic.** Failures appear in enum declaration order, which is why the library's tests can assert an exact message length (see [Test Suite and Fixture Packages](../testing/test-suite.md)).
- **Every call re-imports classes.** There is no caching between calls; each `testArchitecture` call imports the packages again.
- **Exclusions only remove rules.** There is no way to add rules, change a rule's severity, or freeze existing violations through this API. The README leaves freezing up to each team.

## Related

- [Architecture Overview](overview.md)
- [Rule Catalog](../rules/rule-catalog.md)
