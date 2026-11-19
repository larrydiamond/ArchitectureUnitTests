package com.ldiamond.archunittest;

/**
 * Cross-cutting labels attached to {@link ArchitectureRule}s.
 * <p>
 * Unlike {@link RuleCategory}, a rule can carry any number of tags. The rules tree report
 * ({@link RuleTreeReport}) can regroup the rules by tag and filter on them.
 */
public enum RuleTag {

    /**
     * Rule originates from ArchUnit's bundled library rather than this project.
     */
    ARCHUNIT_LIBRARY("ArchUnit library"),
    /**
     * Spring Framework / Spring Boot.
     */
    SPRING("Spring"),
    /**
     * Java Persistence API.
     */
    JPA("JPA"),
    /**
     * Targets the jakarta.* namespace.
     */
    JAKARTA("jakarta.*"),
    /**
     * Targets the legacy javax.* namespace.
     */
    JAVAX("javax.*"),
    /**
     * Targets JSpecify annotations.
     */
    JSPECIFY("JSpecify"),
    /**
     * JUnit.
     */
    JUNIT("JUnit"),
    /**
     * Google Guava.
     */
    GUAVA("Guava"),
    /**
     * Apache Commons.
     */
    APACHE_COMMONS("Apache Commons"),
    /**
     * Runtime performance.
     */
    PERFORMANCE("Performance"),
    /**
     * Threads and synchronization.
     */
    CONCURRENCY("Concurrency"),
    /**
     * Collections framework.
     */
    COLLECTIONS("Collections"),
    /**
     * Naming of classes, methods and fields.
     */
    NAMING("Naming"),
    /**
     * Booleans and their accessors.
     */
    BOOLEANS("Booleans"),
    /**
     * Null handling.
     */
    NULL_SAFETY("Null safety"),
    /**
     * Dependency direction between layers.
     */
    LAYERING("Layering"),
    /**
     * REST / web endpoints.
     */
    REST("REST"),
    /**
     * Behaviour implemented through Spring proxies.
     */
    PROXIES("Proxies"),
    /**
     * Dependency injection.
     */
    DEPENDENCY_INJECTION("Dependency injection"),
    /**
     * Logging and console output.
     */
    LOGGING("Logging"),
    /**
     * Exceptions and assertions.
     */
    EXCEPTIONS("Exceptions"),
    /**
     * Deprecated or discouraged APIs.
     */
    DEPRECATED_APIS("Deprecated APIs"),
    /**
     * Dates and times.
     */
    DATE_TIME("Date & time"),
    /**
     * Test code.
     */
    TESTING("Testing"),
    /**
     * Object-oriented design and domain modelling.
     */
    DESIGN("Design");

    private final String label;

    RuleTag(final String label) {
        this.label = label;
    }

    /**
     * Human readable name
     *
     * @return label
     */
    public String getLabel() {
        return label;
    }
}
