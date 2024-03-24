# Architecture Unit Tests Easy Access Java Library

We love Archunit.   They did a great job giving the rest of us the ability to unit test our architecture.   I use this on every project now.   What isn't in the archunit library right now is "the last mile" - they created the tooling to create great architecture unit tests and a number of great samples, but then it's up to all of us to actually figure out what tests we want to run.   This "last mile" is what this library hopes to solve by providing an easy way to execute a list of rules that we hope to expand upon in the future.

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
    <version>0.1.0</version>
</dependency>
```

### Usage

1. Import the necessary class from the library into your test classes:

```java
import com.ldiamond.archunittest.ArchitectureUnitTest;
```

2. Define your architectural rules and configurations:

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

## Contributing

We welcome contributions from the community to enhance the functionality and usability of this library. If you have any suggestions, bug reports, or feature requests, please open an issue or submit a pull request on GitHub.

## License

This library is licensed under the Apache 2.0 License. See the [LICENSE](LICENSE) file for details.

## Contact

For any inquiries or support, feel free to contact us at ldiamond AT ldiamond DOT com.

---

Thank you again to the ArchUnit team for creating such a great tool.   My preference would be to integrate this work into ArchUnit directly rather than provide a separate library, email me - lets talk.
