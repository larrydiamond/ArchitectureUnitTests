package com.ldiamond.archcommon;

import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.GeneralCodingRules.ASSERTIONS_SHOULD_HAVE_DETAIL_MESSAGE;
import static com.tngtech.archunit.library.GeneralCodingRules.DEPRECATED_API_SHOULD_NOT_BE_USED;
import static com.tngtech.archunit.library.GeneralCodingRules.NO_CLASSES_SHOULD_ACCESS_STANDARD_STREAMS;
import static com.tngtech.archunit.library.GeneralCodingRules.NO_CLASSES_SHOULD_THROW_GENERIC_EXCEPTIONS;
import static com.tngtech.archunit.library.GeneralCodingRules.NO_CLASSES_SHOULD_USE_FIELD_INJECTION;
import static com.tngtech.archunit.library.GeneralCodingRules.NO_CLASSES_SHOULD_USE_JAVA_UTIL_LOGGING;
import static com.tngtech.archunit.library.GeneralCodingRules.NO_CLASSES_SHOULD_USE_JODATIME;

public enum ArchitectureRule {

    ARCHUNIT_ASSERTIONS_SHOULD_HAVE_DETAIL_MESSAGE (ASSERTIONS_SHOULD_HAVE_DETAIL_MESSAGE),
    ARCHUNIT_DEPRECATED_API_SHOULD_NOT_BE_USED (DEPRECATED_API_SHOULD_NOT_BE_USED),
    ARCHUNIT_NO_CLASSES_SHOULD_USE_FIELD_INJECTION (NO_CLASSES_SHOULD_USE_FIELD_INJECTION),
    ARCHUNIT_NO_CLASSES_SHOULD_USE_JAVA_UTIL_LOGGING (NO_CLASSES_SHOULD_USE_JAVA_UTIL_LOGGING),
    ARCHUNIT_NO_CLASSES_SHOULD_THROW_GENERIC_EXCEPTIONS (NO_CLASSES_SHOULD_THROW_GENERIC_EXCEPTIONS),
    ARCHUNIT_NO_CLASSES_SHOULD_ACCESS_STANDARD_STREAMS (NO_CLASSES_SHOULD_ACCESS_STANDARD_STREAMS),
    ARCHUNIT_NO_CLASSES_SHOULD_USE_JODATIME (NO_CLASSES_SHOULD_USE_JODATIME),
    USE_ARRAYLIST_INSTEAD_OF_VECTOR (noClasses().should().callConstructor(java.util.Vector.class).because("Use ArrayList instead of Vector because Arraylist is unsynchronized and faster")),
    USE_HASHMAP_INSTEAD_OF_HASHTABLE (noClasses().should().callConstructor(java.util.Hashtable.class).because("Use HashMap instead of HashTable because HashMap is unsynchronized and faster")),
    USE_STRINGBUILDER_INSTEAD_OF_STRINGBUFFER (noClasses().should().callConstructor(java.lang.StringBuffer.class).because("Use StringBuilder instead of StringBuffer because StringBuilder is unsynchronized and faster")),
    USE_CONCURRENTSKIPLISTSET_INSTEAD_OF_TREESET (noClasses().should().callConstructor(java.util.TreeSet.class).because("Use ConcurrentSkipListSet instead of TreeSet because the SkipList algo is faster than Balanced Tree")),
    USE_CONCURRENTSKIPLISTMAP_INSTEAD_OF_TREEMAP (noClasses().should().callConstructor(java.util.TreeMap.class).because("Use ConcurrentSkipListMap instead of TreeMap because the SkipList algo is faster than Balanced Tree")),
    USE_EXECUTORSERVICE_INSTEAD_OF_THREADGROUP (noClasses().should().callConstructor(java.lang.ThreadGroup.class).because("Use ExecutorService instead of ThreadGroup")),
    SPRING_BOOT_SERVICES_SHOULD_NOT_CALL_CONTROLLERS (noClasses().that().resideInAPackage("..service..").should().dependOnClassesThat().resideInAPackage("..controller..").allowEmptyShould(true)),
    SPRING_BOOT_REPOSITORIES_SHOULD_NOT_CALL_CONTROLLERS (noClasses().that().resideInAPackage("..repository..").should().dependOnClassesThat().resideInAPackage("..controller..").allowEmptyShould(true)),
    SPRING_BOOT_REPOSITORIES_SHOULD_NOT_CALL_SERVICES (noClasses().that().resideInAPackage("..repository..").should().dependOnClassesThat().resideInAPackage("..service..").allowEmptyShould(true));

    private final ArchRule rule;

    private ArchitectureRule (ArchRule ruleInput) { this.rule = ruleInput; }

    public ArchRule getRule () { return rule; }
}

