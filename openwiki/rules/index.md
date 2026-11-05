# Files

- [Custom Conditions and Predicates](custom-conditions.md) - The two hand-written ArchUnit extensions in ArchitectureRule — the same-class self-invocation condition behind the Spring @Cacheable/@Transactional rules, and the primitive-boolean return-type predicate.
- [Known Rule Issues and Gaps](known-issues.md) - Verified defects and surprises in the current ArchitectureRule definitions — Optional rules that can never match, copy-pasted because messages, Spring rules based on package names, tuple rule scope — and the rules that no test exercises.
- [Rule Catalog](rule-catalog.md) - All 43 ArchitectureRule enum constants grouped by concern, with what each group matches, how empty matches and because-messages are handled, and the version each group arrived in.
