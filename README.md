# ArchUnit Easy Access Java Library

Welcome to the ArchUnit Easy Access Java Library! This library aims to simplify the process of running architecture unit tests using ArchUnit by providing opinionated and easy-to-use utilities. With this library, you can quickly set up and run numerous ArchUnit tests for your projects, ensuring architectural integrity and adherence to predefined rules.

## Features

- **Opinionated Configuration**: Simplifies the setup process by providing predefined configurations for common architectural tests.
- **Easy Integration**: Seamlessly integrates with your existing Java projects, allowing for quick adoption of architectural tests.
- **Comprehensive Test Suite**: Offers a variety of prebuilt ArchUnit tests covering common architectural principles such as layering, package structure, and dependencies.
- **Customization**: While opinionated, the library allows for customization to suit specific project requirements.
- **Quick Start Guides**: Provides clear and concise documentation with examples to help you get started with running architectural tests.

## Getting Started

### Installation

To use the ArchUnit Easy Access Java Library in your project, include the following dependency in your `pom.xml`:

```xml
<dependency>
    <groupId>com.example</groupId>
    <artifactId>archunit-easy-access</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Usage

1. Import the necessary classes from the library into your test classes:

```java
import com.example.archunit.easyaccess.ArchUnitTestRunner;
```

2. Define your architectural rules and configurations:

```java
public class MyArchitectureTests {

    @Test
    public void testLayeringRules() {
        ArchUnitTestRunner.runLayeringTests("com.example.myproject");
    }

    @Test
    public void testPackageStructure() {
        ArchUnitTestRunner.runPackageStructureTests("com.example.myproject");
    }

    // Add more tests as needed
}
```

3. Run your tests using your preferred test runner (e.g., JUnit):

```java
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
    MyArchitectureTests.class,
    // Add more test classes if necessary
})
public class ArchitectureTestSuite {
    // This class can be empty
}
```

4. Execute your test suite to ensure architectural compliance:

```
mvn test
```

## Contributing

We welcome contributions from the community to enhance the functionality and usability of this library. If you have any suggestions, bug reports, or feature requests, please open an issue or submit a pull request on GitHub.

## License

This library is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

## Contact

For any inquiries or support, feel free to contact us at [email@example.com](mailto:email@example.com).

---

Thank you for choosing the ArchUnit Easy Access Java Library. We hope it helps streamline your architecture testing process and ensures the integrity of your projects' designs. Happy testing!
