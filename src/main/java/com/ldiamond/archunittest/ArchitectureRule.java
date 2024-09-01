/*
 * #%L
 * Standardized ArchUnit tests
 * %%
 * Copyright (C) 2024 Larry Diamond
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package com.ldiamond.archunittest;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.properties.CanBeAnnotated.Predicates;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noFields;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noMethods;
import static com.tngtech.archunit.library.GeneralCodingRules.ASSERTIONS_SHOULD_HAVE_DETAIL_MESSAGE;
import static com.tngtech.archunit.library.GeneralCodingRules.DEPRECATED_API_SHOULD_NOT_BE_USED;
import static com.tngtech.archunit.library.GeneralCodingRules.NO_CLASSES_SHOULD_ACCESS_STANDARD_STREAMS;
import static com.tngtech.archunit.library.GeneralCodingRules.NO_CLASSES_SHOULD_THROW_GENERIC_EXCEPTIONS;
import static com.tngtech.archunit.library.GeneralCodingRules.NO_CLASSES_SHOULD_USE_FIELD_INJECTION;
import static com.tngtech.archunit.library.GeneralCodingRules.NO_CLASSES_SHOULD_USE_JAVA_UTIL_LOGGING;
import static com.tngtech.archunit.library.GeneralCodingRules.NO_CLASSES_SHOULD_USE_JODATIME;

/**
 * This enum contains all the arch rules that this library includes.   Please note that over time we expect this enum to expand as more good ideas are accepted.
 */
public enum ArchitectureRule {

    /**
     * Archunit defined rule: A rule that checks that all AssertionErrors have a detail message.
     */
    ARCHUNIT_ASSERTIONS_SHOULD_HAVE_DETAIL_MESSAGE (ASSERTIONS_SHOULD_HAVE_DETAIL_MESSAGE),

    /**
     * Archunit defined rule: A rule checking that no class accesses Deprecated members
     */
    ARCHUNIT_DEPRECATED_API_SHOULD_NOT_BE_USED (DEPRECATED_API_SHOULD_NOT_BE_USED),

    /**
     * Archunit defined rule: A rule that checks that none of the given classes uses field injection.
     */
    ARCHUNIT_NO_CLASSES_SHOULD_USE_FIELD_INJECTION (NO_CLASSES_SHOULD_USE_FIELD_INJECTION.allowEmptyShould(true)),

    /**
     * Archunit defined rule: A rule that checks that none of the given classes access Java Util Logging.
     */
    ARCHUNIT_NO_CLASSES_SHOULD_USE_JAVA_UTIL_LOGGING (NO_CLASSES_SHOULD_USE_JAVA_UTIL_LOGGING),

    /**
     * Archunit defined rule: A rule that checks that none of the given classes throw generic exceptions like Exception, RuntimeException, or Throwable.
     */
    ARCHUNIT_NO_CLASSES_SHOULD_THROW_GENERIC_EXCEPTIONS (NO_CLASSES_SHOULD_THROW_GENERIC_EXCEPTIONS),

    /**
     * Archunit defined rule: A rule that checks that none of the given classes access the standard streams System.out and System.err.
     */
    ARCHUNIT_NO_CLASSES_SHOULD_ACCESS_STANDARD_STREAMS (NO_CLASSES_SHOULD_ACCESS_STANDARD_STREAMS),

    /**
     * Archunit defined rule: A rule that checks that none of the given classes access JodaTime.
     */
    ARCHUNIT_NO_CLASSES_SHOULD_USE_JODATIME (NO_CLASSES_SHOULD_USE_JODATIME),

    /**
     * This rule recommends replacing Vector with ArrayList since Vector is synchronized and generally slower than ArrayList
     */
    USE_ARRAYLIST_INSTEAD_OF_VECTOR (noClasses().should().callConstructor(java.util.Vector.class).because("Use ArrayList instead of Vector because Arraylist is unsynchronized and faster")),

    /**
     * This rule recommends replacing HashTable with HashMap since HashTable is synchronized and generally slower than HashMap
     */
    USE_HASHMAP_INSTEAD_OF_HASHTABLE (noClasses().should().callConstructor(java.util.Hashtable.class).because("Use HashMap instead of HashTable because HashMap is unsynchronized and faster")),

    /**
     * This rule recommends replacing StringBuffer with StringBuilder since StringBuffer is synchronized and generally slower than StringBuilder
     */
    USE_STRINGBUILDER_INSTEAD_OF_STRINGBUFFER (noClasses().should().callConstructor(java.lang.StringBuffer.class).because("Use StringBuilder instead of StringBuffer because StringBuilder is unsynchronized and faster")),

    /**
     * Skip lists are faster than balanced trees
     */
    USE_CONCURRENTSKIPLISTSET_INSTEAD_OF_TREESET (noClasses().should().callConstructor(java.util.TreeSet.class).because("Use ConcurrentSkipListSet instead of TreeSet because the SkipList algo is faster than Balanced Tree")),

    /**
     * Skip lists are faster than balanced trees
     */
    USE_CONCURRENTSKIPLISTMAP_INSTEAD_OF_TREEMAP (noClasses().should().callConstructor(java.util.TreeMap.class).because("Use ConcurrentSkipListMap instead of TreeMap because the SkipList algo is faster than Balanced Tree")),

    /**
     * Thread groups have long been recommended against, with ExecutorService classes being recommended as their replacement
     */
    USE_EXECUTORSERVICE_INSTEAD_OF_THREADGROUP (noClasses().should().callConstructor(java.lang.ThreadGroup.class).because("Use ExecutorService instead of ThreadGroup")),

    /**
     * This rule prevents Spring Boot service classes from calling controller classes
     */
    SPRING_BOOT_SERVICES_SHOULD_NOT_CALL_CONTROLLERS (noClasses().that().resideInAPackage("..service..").should().dependOnClassesThat().resideInAPackage("..controller..").allowEmptyShould(true).because("Spring Boot Services should not call Controllers")),

    /**
     * This rule prevents Spring Boot repository classes from calling controller classes
     */
    SPRING_BOOT_REPOSITORIES_SHOULD_NOT_CALL_CONTROLLERS (noClasses().that().resideInAPackage("..repository..").should().dependOnClassesThat().resideInAPackage("..controller..").allowEmptyShould(true).because("Spring Boot Repositories should not call Controllers")),

    /**
     * This rule prevents Spring Boot repository classes from calling service classes
     */
    SPRING_BOOT_REPOSITORIES_SHOULD_NOT_CALL_SERVICES (noClasses().that().resideInAPackage("..repository..").should().dependOnClassesThat().resideInAPackage("..service..").allowEmptyShould(true).because("Spring Boot Repositories should not call Services")),

    /**
     * This rule prevents interfaces being named *Impl 
     * Positive tests are testNoImplInterfacesDoesNotFailRuleset and testNoInterfacesDoesNotFailRuleset, negative tests is testImplViolationFailsRuleset
     */
    INTERFACES_SHOULD_NOT_END_IN_IMPL (noClasses().that().haveSimpleNameEndingWith("Impl").should().beInterfaces().allowEmptyShould(true).allowEmptyShould(true).because("Interfaces should not be named Impl")),
    
    /**
     * This rule prevents inappropriate coupling by preventing instances of JPA classes from being returned from Get restful endpoints.   Database table layouts should not forcibly define the restful return formats, there should be data transfer objects that are returned to the clients
     * Positive test is testJpaCohesionViolationDoesNotFailRuleset, negative test is testJpaCohesionViolationFailsRuleset
     */
    JPA_COUPLING_RESTFUL_GET_MAPPINGS (noMethods().that().areAnnotatedWith("org.springframework.web.bind.annotation.GetMapping").should().haveRawReturnType(Predicates.annotatedWith("jakarta.persistence.Entity")).allowEmptyShould(true).because("JPA_COUPLING_RESTFUL_GET_MAPPINGS REST response types should not be forced to always exactly match JPA Entity types")),

    /**
     * This rule prevents inappropriate coupling by preventing instances of JPA classes from being returned from Get restful endpoints.   Database table layouts should not forcibly define the restful return formats, there should be data transfer objects that are returned to the clients
     */
    JPA_COUPLING_RESTFUL_GET_MAPPINGS_JAVAX (noMethods().that().areAnnotatedWith("org.springframework.web.bind.annotation.GetMapping").should().haveRawReturnType(Predicates.annotatedWith("javax.persistence.Entity")).allowEmptyShould(true).because("JPA_COUPLING_RESTFUL_GET_MAPPINGS_JAVAX REST response types should not be forced to always exactly match JPA Entity types")),

    /**
     * This rule prevents inappropriate coupling by preventing instances of JPA classes from being returned from restful endpoints.   Database table layouts should not forcibly define the restful return formats, there should be data transfer objects that are returned to the clients
     */
    JPA_COUPLING_RESTFUL_REQUEST_MAPPINGS (noMethods().that().areAnnotatedWith("org.springframework.web.bind.annotation.RequestMapping").should().haveRawReturnType(Predicates.annotatedWith("jakarta.persistence.Entity")).allowEmptyShould(true).because("JPA_COUPLING_RESTFUL_REQUEST_MAPPINGS REST response types should not be forced to always exactly match JPA Entity types")),

    /**
     * This rule prevents inappropriate coupling by preventing instances of JPA classes from being returned from restful endpoints.   Database table layouts should not forcibly define the restful return formats, there should be data transfer objects that are returned to the clients
     */
    JPA_COUPLING_RESTFUL_REQUEST_MAPPINGS_JAVAX (noMethods().that().areAnnotatedWith("org.springframework.web.bind.annotation.RequestMapping").should().haveRawReturnType(Predicates.annotatedWith("javax.persistence.Entity")).allowEmptyShould(true).because("JPA_COUPLING_RESTFUL_REQUEST_MAPPINGS_JAVAX REST response types should not be forced to always exactly match JPA Entity types")),
    
    /**
     * This rule ensures isSomething methods return primitive boolean
     * Positive test is testIsMethodReturnsPrimitiveBooleanDoesNotFailRuleset, negative test is testIsMethodReturnsPrimitiveBooleanFailsRuleset
     */
     IS_METHODS_RETURN_PRIMITIVE_BOOLEAN (methods().that().haveNameMatching("is[A-Z][a-zA-Z]+").should().haveRawReturnType(new DescribedPredicate<JavaClass>("primitive boolean") {
        @Override
        public boolean test(JavaClass input) {
            return input.isPrimitive() && "boolean".equals(input.getName());
        }
     }).because ("Is methods should return primitive boolean - Boolean could return null").allowEmptyShould(true)),

     /**
      * This rule recommends against field named isBlah since they're confusing and should just be blah.   blah setter and getter would be setBlah and isBlah, isBlah setter and getter would be setIsBlah and isIsBlah which is confusing
      * Positive test method is testFieldsThatArentIsBlahIsGood, negative test method is testFieldsThatAreIsBlahIsBad
      */
      RENAME_IS_FIELDS (noFields().should().haveNameMatching("is[A-Z][a-zA-Z]+").because("isBlah should be rewritten as blah, getter on isBlah would be isIsBlah which is confusing").allowEmptyShould(true)),

    /**
     * This rule encourages positive boolean methods to avoid confusing if (!isNotSomething()) methods 
     * Positive test method is testRestateNegativeIsNotDoesNotFailRuleset, negative test method is testRestateNegativeIsNotFailsRuleset
     */
     RESTATE_ISNOT_METHODS_AS_POSITIVE (noMethods().that().haveNameMatching("isNot[A-Z][a-zA-Z]+").should().haveNameStartingWith("isNot").because("isNot methods should be rewritten as positive conditions since if (!isNotSomething() is confusing").allowEmptyShould(true)),
     
     /**
      * Optional methods should return Empty and not be nullable.
      */
      OPTIONAL_NOT_NULLABLE (noMethods().that().areAnnotatedWith("jakarta.annotation.Nullable").should().haveRawReturnType(Predicates.annotatedWith("java.util.Optional")).allowEmptyShould(true).because("OPTIONAL_NOT_NULLABLE Optional methods should return Optional.empty not null")),

     /**
      * Optional methods should return Empty and not be nullable.
      */
      OPTIONAL_NOT_NULLABLE_JAVAX (noMethods().that().areAnnotatedWith("javax.annotation.Nullable").should().haveRawReturnType(Predicates.annotatedWith("java.util.Optional")).allowEmptyShould(true).because("OPTIONAL_NOT_NULLABLE_JAVAX Optional methods should return Optional.empty not null")),

     /**
      * Optional methods should return Empty and not be nullable.
      */
      OPTIONAL_NOT_NULLABLE_JSPECIFY (noMethods().that().areAnnotatedWith("org.jspecify.annotations.Nullable").should().haveRawReturnType(Predicates.annotatedWith("java.util.Optional")).allowEmptyShould(true).because("OPTIONAL_NOT_NULLABLE_JSPECIFY Optional methods should return Optional.empty not null")),

     /**
      * This rule prevents Spring Boot service classes from calling controller methods
      * Postive test is testControllerCallingServiceCallingRepositoryIsGood, Negative test is testServiceCallingControllerIsBad
      */
      SPRING_BOOT_SERVICES_SHOULD_NOT_CALL_CONTROLLER_METHODS (noClasses().that().areAnnotatedWith("org.springframework.stereotype.Service").should().dependOnClassesThat().areAnnotatedWith("org.springframework.stereotype.Controller").allowEmptyShould(true).because("Spring Boot Services should not call Controller methods")),

      /**
      * This rule prevents Spring Boot repository classes from calling controller methods
      * Postive test is testControllerCallingServiceCallingRepositoryIsGood, Negative test is testRepositoryCallingControllerIsBad
      */
      SPRING_BOOT_REPOSITORIES_SHOULD_NOT_CALL_CONTROLLER_METHODS (noClasses().that().areAnnotatedWith("org.springframework.stereotype.Repository").should().dependOnClassesThat().areAnnotatedWith("org.springframework.stereotype.Controller").allowEmptyShould(true).because("Spring Boot Repositories should not call Controller methods")),

      /**
      * This rule prevents Spring Boot repository classes from calling service methods
      * Postive test is testControllerCallingServiceCallingRepositoryIsGood, Negative test is testRepositoryCallingServiceIsBad
      */
      SPRING_BOOT_REPOSITORIES_SHOULD_NOT_CALL_SERVICE_METHODS (noClasses().that().areAnnotatedWith("org.springframework.stereotype.Repository").should().dependOnClassesThat().areAnnotatedWith("org.springframework.stereotype.Service").allowEmptyShould(true).because("Spring Boot Repositories should not call Service methods"))
      ;

    private final ArchRule rule;

    private ArchitectureRule (ArchRule ruleInput) { this.rule = ruleInput; }

    /**
     * Obtains the Archrule being checked
     * @return Archrule
     */
    public ArchRule getRule () { return rule; }
}

