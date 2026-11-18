package com.ldiamond.archunittest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Hierarchical grouping of {@link ArchitectureRule}s.
 * <p>
 * Every rule belongs to exactly one category. Categories form a tree through their optional parent,
 * which is what the rules tree report ({@link RuleTreeReport}) uses as its primary navigation structure.
 * A parent must be declared before its children.
 */
public enum RuleCategory {

    /**
     * Language-level coding practices that apply to any Java code base.
     */
    GENERAL_CODING(null, "General coding practices", "Language-level practices that apply to any Java code base"),

    /**
     * Rules reused from ArchUnit's own {@code GeneralCodingRules} library.
     */
    ARCHUNIT_GENERAL_CODING_RULES(GENERAL_CODING, "ArchUnit general coding rules", "Rules reused from ArchUnit's GeneralCodingRules library"),

    /**
     * Naming conventions, mostly around boolean accessors.
     */
    NAMING(GENERAL_CODING, "Naming conventions", "Names that read naturally and avoid confusing accessors"),

    /**
     * Prefer Optional.empty() over null.
     */
    NULL_SAFETY(GENERAL_CODING, "Null safety", "Optional values should never be null themselves"),

    /**
     * Performance and concurrency.
     */
    PERFORMANCE(null, "Performance & concurrency", "Prefer faster, modern JDK classes over legacy ones"),

    /**
     * Legacy JDK classes that synchronize every call.
     */
    SYNCHRONIZED_LEGACY_CLASSES(PERFORMANCE, "Synchronized legacy classes", "Legacy JDK classes that synchronize every call"),

    /**
     * Sorted collections backed by balanced trees.
     */
    SORTED_COLLECTIONS(PERFORMANCE, "Sorted collections", "Skip lists generally outperform balanced trees"),

    /**
     * Thread management.
     */
    THREADING(PERFORMANCE, "Threading", "Modern thread management"),

    /**
     * Spring and Spring Boot.
     */
    SPRING(null, "Spring", "Spring and Spring Boot conventions"),

    /**
     * Dependency direction between controllers, services and repositories.
     */
    SPRING_LAYERING(SPRING, "Layering", "Dependencies must point from controllers to services to repositories"),

    /**
     * Layering detected through package names.
     */
    SPRING_LAYERING_BY_PACKAGE(SPRING_LAYERING, "By package name", "Layers detected from ..controller.., ..service.. and ..repository.. packages"),

    /**
     * Layering detected through stereotype annotations.
     */
    SPRING_LAYERING_BY_ANNOTATION(SPRING_LAYERING, "By stereotype annotation", "Layers detected from @Controller, @Service and @Repository"),

    /**
     * Web request mappings.
     */
    SPRING_WEB(SPRING, "Web endpoints", "Request mappings belong in controllers"),

    /**
     * Proxy-based features that silently stop working when misused.
     */
    SPRING_PROXIES(SPRING, "Proxy pitfalls", "Proxy-based features that silently stop working when misused"),

    /**
     * Persistence and API boundaries.
     */
    PERSISTENCE(null, "Persistence & API boundaries", "Keep database models out of public APIs"),

    /**
     * JPA entities returned from REST endpoints.
     */
    JPA_ENTITY_EXPOSURE(PERSISTENCE, "JPA entities in REST responses", "Return DTOs, not JPA entities, from REST endpoints"),

    /**
     * Test code.
     */
    TESTING(null, "Testing", "Tests that actually run"),

    /**
     * JUnit 5 specifics.
     */
    JUNIT5(TESTING, "JUnit 5", "Test methods JUnit 5 silently ignores"),

    /**
     * Third-party library usage.
     */
    THIRD_PARTY_LIBRARIES(null, "Third-party libraries", "APIs their own maintainers discourage, or that hide intent"),

    /**
     * Google Guava.
     */
    GUAVA(THIRD_PARTY_LIBRARIES, "Google Guava", "Guava APIs with better replacements"),

    /**
     * Apache Commons.
     */
    APACHE_COMMONS(THIRD_PARTY_LIBRARIES, "Apache Commons", "Apache Commons APIs that hide intent");

    private final RuleCategory parent;
    private final String label;
    private final String description;

    RuleCategory(final RuleCategory parent, final String label, final String description) {
        this.parent = parent;
        this.label = label;
        this.description = description;
    }

    /**
     * The enclosing category
     *
     * @return the parent category, or empty for a top-level category
     */
    public Optional<RuleCategory> getParent() {
        return Optional.ofNullable(parent);
    }

    /**
     * Human readable name
     *
     * @return label
     */
    public String getLabel() {
        return label;
    }

    /**
     * One-line explanation of what the category groups
     *
     * @return description
     */
    public String getDescription() {
        return description;
    }

    /**
     * The chain of categories from the top-level category down to this one (inclusive)
     *
     * @return unmodifiable path, root first
     */
    public List<RuleCategory> getPath() {
        List<RuleCategory> path = new ArrayList<>();
        for (RuleCategory c = this; c != null; c = c.parent) {
            path.add(0, c);
        }
        return Collections.unmodifiableList(path);
    }
}
