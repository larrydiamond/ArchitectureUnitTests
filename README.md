# Architecture Unit Tests Easy Access Java Library

[![Maven Central Version](https://img.shields.io/maven-central/v/com.ldiamond/architectureunittests)](https://central.sonatype.com/artifact/com.ldiamond/architectureunittests)
[![GitHub Release](https://img.shields.io/github/v/release/larrydiamond/ArchitectureUnitTests)](https://github.com/larrydiamond/ArchitectureUnitTests/releases)
[![GitHub License](https://img.shields.io/github/license/larrydiamond/architectureunittests)](https://github.com/larrydiamond/ArchitectureUnitTests/blob/main/LICENSE)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=larrydiamond_ArchitectureUnitTests&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=larrydiamond_ArchitectureUnitTests)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=larrydiamond_ArchitectureUnitTests&metric=bugs)](https://sonarcloud.io/summary/new_code?id=larrydiamond_ArchitectureUnitTests)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=larrydiamond_ArchitectureUnitTests&metric=code_smells)](https://sonarcloud.io/summary/new_code?id=larrydiamond_ArchitectureUnitTests)
[![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=larrydiamond_ArchitectureUnitTests&metric=sqale_index)](https://sonarcloud.io/summary/new_code?id=larrydiamond_ArchitectureUnitTests)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=larrydiamond_ArchitectureUnitTests&metric=vulnerabilities)](https://sonarcloud.io/summary/new_code?id=larrydiamond_ArchitectureUnitTests)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=larrydiamond_ArchitectureUnitTests&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=larrydiamond_ArchitectureUnitTests)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=larrydiamond_ArchitectureUnitTests&metric=reliability_rating)](https://sonarcloud.io/summary/new_code?id=larrydiamond_ArchitectureUnitTests)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=larrydiamond_ArchitectureUnitTests&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=larrydiamond_ArchitectureUnitTests)
[![Codacy Badge](https://app.codacy.com/project/badge/Grade/79803a703e0c4fcd95caa28c0ff1978a)](https://app.codacy.com/gh/larrydiamond/ArchitectureUnitTests/dashboard?utm_source=gh&utm_medium=referral&utm_content=&utm_campaign=Badge_grade)

We love [Archunit](https://www.archunit.org).   They did a great job giving the rest of us the ability to unit test our architecture.   I use this on every project now.   What isn't in the archunit library right now is "the last mile" - they created the tooling to create great architecture unit tests and a number of great samples, but then it's up to all of us to actually figure out what tests we want to run.   This "last mile" is what this library hopes to solve by providing an easy way to execute a list of **reasonable common sense** rules that we hope to expand upon in the future.

By providing a call that applies each of our architecture rules, this provides the ability to add more rules later and projects will just apply them as they upgrade versions of this library.

## Features

- **Opinionated Configuration**: Simplifies the setup process by providing a predefined collection of common architectural tests (we expect to add more over time).
- **Easy Integration**: Seamlessly integrates with your existing Java projects, allowing for quick adoption of architectural tests.
- **Exclusions**: Some projects are going to violate some of these rules, and we provide a quick and easy way to exclude rules that don't work for your project.

## Getting Started

### Installation

To use the ArchUnit Easy Access Java Library in your project, include the following dependency in your `pom.xml`:

```xml
<dependency>
    <groupId>com.ldiamond</groupId>
    <artifactId>architectureunittests</artifactId>
    <version>2.0.0</version>
</dependency>
```

or in your `build.gradle`:
```xml
testImplementation 'com.ldiamond:architectureunittests:2.0.0'
```

or

[https://central.sonatype.com/artifact/com.ldiamond/architectureunittests](https://central.sonatype.com/artifact/com.ldiamond/architectureunittests)

[https://mvnrepository.com/artifact/com.ldiamond/architectureunittests](https://mvnrepository.com/artifact/com.ldiamond/architectureunittests)


### Usage

1. Import the necessary class from the library into your test classes:

```java
import com.ldiamond.archunittest.ArchitectureUnitTest;
```

This library creates no transitive dependencies for your consumers, and this library only has a transitive dependency on ArchUnit itself.

2. Test the rules on your classes:

```java
public class MyArchitectureTests {

    @Test
    public void runArchitectureTests() {
        ArchitectureUnitTest.testArchitecture("com.example.myproject");
    }
}
```

3. Execute your test suite to ensure architectural compliance:

```
mvn test
```
or
```
gradle test
```

## How to exclude some architecture tests when running

This unit tests will fail if any of the architecture rules are violated.   These tests are reasonable and common sense, but valid exceptions to many of the rules will exist.   If the testArchitecture call fails and you decide that the failing test should be ignored, then modify your code to exclude the offending ArchRules from the test.

1. Import the necessary classes from the library into your test classes:

```java
import com.ldiamond.archunittest.ArchitectureRule;
import com.ldiamond.archunittest.ArchitectureUnitTest;
import java.util.Arrays;
```

2. List the architectural rules to exclude:

```java
public class MyArchitectureTests {

    @Test
    public void runArchitectureTests() {
        ArchitectureUnitTest.testArchitecture(
            Arrays.asList(ArchitectureRule.USE_ARRAYLIST_INSTEAD_OF_VECTOR), 
            "com.example.myproject");
    }
}
```

3. Execute your test suite to ensure architectural compliance:

```
mvn test
```

## What architecture tests do we have today?

| How to Exclude  | What this rule does                                                                                                                                                                                                                                                                                                                                                          |
| ------------- |------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| ARCHUNIT_ASSERTIONS_SHOULD_HAVE_DETAIL_MESSAGE | A rule that checks that all AssertionErrors have a detail message                                                                                                                                                                                                                                                                                                            |
| ARCHUNIT_DEPRECATED_API_SHOULD_NOT_BE_USED | A rule checking that no class accesses Deprecated members                                                                                                                                                                                                                                                                                                                    |
| ARCHUNIT_NO_CLASSES_SHOULD_USE_FIELD_INJECTION | A rule that checks that none of the given classes uses field injection                                                                                                                                                                                                                                                                                                       |
| ARCHUNIT_NO_CLASSES_SHOULD_USE_JAVA_UTIL_LOGGING | A rule that checks that none of the given classes access Java Util Logging                                                                                                                                                                                                                                                                                                   |
| ARCHUNIT_NO_CLASSES_SHOULD_THROW_GENERIC_EXCEPTIONS | A rule that checks that none of the given classes throw generic exceptions like Exception, RuntimeException, or Throwable                                                                                                                                                                                                                                                    |
| ARCHUNIT_NO_CLASSES_SHOULD_ACCESS_STANDARD_STREAMS | A rule that checks that none of the given classes access the standard streams System.out and System.err                                                                                                                                                                                                                                                                      |
| ARCHUNIT_NO_CLASSES_SHOULD_USE_JODATIME | A rule that checks that none of the given classes access JodaTime                                                                                                                                                                                                                                                                                                            |
| USE_ARRAYLIST_INSTEAD_OF_VECTOR  | A rule that checks that none of the given classes access Vector                                                                                                                                                                                                                                                                                                              |
| USE_HASHMAP_INSTEAD_OF_HASHTABLE  | A rule that checks that none of the given classes access HashTable                                                                                                                                                                                                                                                                                                           |
| USE_STRINGBUILDER_INSTEAD_OF_STRINGBUFFER  | A rule that checks that none of the given classes access StringBuffer                                                                                                                                                                                                                                                                                                        |
| USE_CONCURRENTSKIPLISTSET_INSTEAD_OF_TREESET  | A rule that checks that none of the given classes access TreeSet                                                                                                                                                                                                                                                                                                             |
| USE_CONCURRENTSKIPLISTMAP_INSTEAD_OF_TREEMAP  | A rule that checks that none of the given classes access TreeMap                                                                                                                                                                                                                                                                                                             |
| USE_EXECUTORSERVICE_INSTEAD_OF_THREADGROUP  | A rule that checks that none of the given classes access ThreadGroup                                                                                                                                                                                                                                                                                                         |
| SPRING_BOOT_SERVICES_SHOULD_NOT_CALL_CONTROLLERS  | A rule that checks that Spring Boot Services do not access Spring Boot Controllers                                                                                                                                                                                                                                                                                           |
| SPRING_BOOT_REPOSITORIES_SHOULD_NOT_CALL_CONTROLLERS  | A rule that checks that Spring Boot Repositories do not access Spring Boot Controllers                                                                                                                                                                                                                                                                                       |
| SPRING_BOOT_REPOSITORIES_SHOULD_NOT_CALL_SERVICES  | A rule that checks that Spring Boot Repositories do not access Spring Boot Services                                                                                                                                                                                                                                                                                          |
| INTERFACES_SHOULD_NOT_BE_CALLED_IMPL  | A rule that ensures that interfaces are not named Impl                                                                                                                                                                                                                                                                                                                       |
| JPA_COUPLING_RESTFUL_GET_MAPPINGS  | This rule prevents inappropriate coupling by preventing instances of JPA classes from being returned from Get restful endpoints.   Database table layouts should not forcibly define the restful return formats, there should be data transfer objects that are returned to the clients                                                                                      |
| JPA_COUPLING_RESTFUL_GET_MAPPINGS_JAVAX  | This rule prevents inappropriate coupling by preventing instances of JPA classes from being returned from Get restful endpoints using the older javax annotation rather than the newer jakarta annotation.   Database table layouts should not forcibly define the restful return formats, there should be data transfer objects that are returned to the clients            |
| JPA_COUPLING_RESTFUL_REQUEST_MAPPINGS  | This rule prevents inappropriate coupling by preventing instances of JPA classes from being returned from RequestMapping restful endpoints.   Database table layouts should not forcibly define the restful return formats, there should be data transfer objects that are returned to the clients                                                                           |
| JPA_COUPLING_RESTFUL_REQUEST_MAPPINGS_JAVAX  | This rule prevents inappropriate coupling by preventing instances of JPA classes from being returned from RequestMapping restful endpoints using the older javax annotation rather than the newer jakarta annotation.   Database table layouts should not forcibly define the restful return formats, there should be data transfer objects that are returned to the clients |
| IS_METHODS_RETURN_PRIMITIVE_BOOLEAN  | This rule ensures isSomething methods return primitive boolean  |
| RESTATE_ISNOT_METHODS_AS_POSITIVE  | This rule encourages positive boolean methods to avoid confusing if (!isNotSomething()) methods     |
| RENAME_IS_FIELDS  | This rule encourages renaming isZZZ fields to be less confusing.   boolean isBlah would have method isIsBlah which is confusing and should be avoided     |
| OPTIONAL_NOT_NULLABLE  | This rule encourages returning Optional.empty() rather than NULL                                                                                                                                                                                                                                                                                                             |
| OPTIONAL_NOT_NULLABLE_JAVAX  | This rule encourages returning Optional.empty() rather than NULL using the older javax annotation rather than the newer jakarta annotation                                                                                                                                                                                                                                   |
| OPTIONAL_NOT_NULLABLE_JSPECIFY  | This rule encourages returning Optional.empty() rather than NULL using the jspecify nullable annotation because [standards](https://xkcd.com/927/)                                                                                                                                                                                                                           |
| SPRING_BOOT_SERVICES_SHOULD_NOT_CALL_CONTROLLER_METHODS  | A rule that checks that Spring Boot Services do not call methods on Spring Boot Controllers                                                                                                                                                                                                                                                                                  |
| SPRING_BOOT_REPOSITORIES_SHOULD_NOT_CALL_CONTROLLER_METHODS  | A rule that checks that Spring Boot Repositories do not call methods on Spring Boot Controllers                                                                                                                                                                                                                                                                                  |
| SPRING_BOOT_REPOSITORIES_SHOULD_NOT_CALL_SERVICE_METHODS  | A rule that checks that Spring Boot Repositories do not call methods on Spring Boot Services                                                                                                                                                                                                                                                                                  |

## Why arent you just freezing rule violations?

Your choice of how to handle violations is up to you.   You may elect to ignore the rule or to freeze violations, but that choice is something your team should discuss.

## Contributing

We welcome contributions from the community to enhance the functionality and usability of this library. If you have any suggestions, bug reports, or feature requests, please open an issue or submit a pull request on GitHub.

No really **WE WANT YOUR GREAT ArchRules**.   This library provides value by preventing bad things from happening to good projects - you can do a lot of good to the Java community by contributing good rules that prevent projects from falling into chaos.

## License

This library is licensed under the Apache 2.0 License. See the [LICENSE](LICENSE) file for details.

This is intentionally the same license as ArchUnit to ease approving this library in your company.

## Contact

For any inquiries or support, feel free to contact us at ldiamond AT ldiamond DOT com.   

This project is not owned by ArchUnit nor do we own ArchUnit.   They don't yet know we exist.   No ownership relationship or management relationship exists.

Thank you again to the ArchUnit team for creating such a great tool.   My preference would be to integrate this work into ArchUnit directly rather than provide a separate library, email me - lets talk.
