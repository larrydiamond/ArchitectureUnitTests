# Files

- [Mutation Testing with PIT](mutation-testing.md) - How PIT mutation testing is configured in the Gradle build, how to run it and read its reports, and why its score covers only the small amount of procedural code in this library.
- [Test Suite and Fixture Packages](test-suite.md) - How the library tests its rules — each JUnit test runs the full rule set against a small fixture package under src/test, asserting either a clean pass or an exact violation message and length.
