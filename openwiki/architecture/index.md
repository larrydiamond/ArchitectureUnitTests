# Files

- [Architecture Overview](overview.md) - How the ArchitectureUnitTests library is structured — a single enum of ArchUnit rules plus a static facade that runs them all against a consumer's packages.
- [Rule Runner and Failure Aggregation](rule-runner.md) - Control flow of ArchitectureUnitTest.testArchitecture — importing classes, skipping excluded rules, checking each rule, and combining every violation into one AssertionError.
