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

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class TestArchitectureUnitTest {
    @Test void testpackageFailsRuleset () {
        AssertionError ae = assertThrowsExactly (AssertionError.class, () -> {
            ArchitectureUnitTest.testArchitecture("com.ldiamond.archunittest.testpackage");
        });
        assertTrue(ae.toString().contains("Architecture Violation [Priority: MEDIUM] - Rule 'no classes should call constructor Vector.<init>(), because Use ArrayList instead of Vector because Arraylist is unsynchronized and faster' was violated (1 times):"));
        assertTrue(ae.toString().contains("Constructor <com.ldiamond.archunittest.testpackage.ClassWithViolationsUnderTest.<init>()> calls constructor <java.util.Vector.<init>()> in (ClassWithViolationsUnderTest.java:26)"));
        assertTrue(ae.toString().contains("Architecture Violation [Priority: MEDIUM] - Rule 'no classes should call constructor Hashtable.<init>(), because Use HashMap instead of HashTable because HashMap is unsynchronized and faster' was violated (1 times):"));
        assertTrue(ae.toString().contains("Constructor <com.ldiamond.archunittest.testpackage.ClassWithViolationsUnderTest.<init>()> calls constructor <java.util.Hashtable.<init>()> in (ClassWithViolationsUnderTest.java:27)"));
        assertEquals(867, ae.toString().length());
    }

    @Test void testpackageFailsRulesetWithExclusionForVectorButNotHashtable () {
        AssertionError ae = assertThrowsExactly (AssertionError.class, () -> {
            ArchitectureUnitTest.testArchitecture(Arrays.asList(ArchitectureRule.USE_ARRAYLIST_INSTEAD_OF_VECTOR), 
            "com.ldiamond.archunittest.testpackage");
        });
        assertTrue(ae.toString().contains("Architecture Violation [Priority: MEDIUM] - Rule 'no classes should call constructor Hashtable.<init>(), because Use HashMap instead of HashTable because HashMap is unsynchronized and faster' was violated (1 times):"));
        assertTrue(ae.toString().contains("Constructor <com.ldiamond.archunittest.testpackage.ClassWithViolationsUnderTest.<init>()> calls constructor <java.util.Hashtable.<init>()> in (ClassWithViolationsUnderTest.java:27)"));
        assertEquals(473, ae.toString().length());
    }

    @Test void testpackageSucceedsRulesetWithExclusionForVectorAndHashtable () {
        ArchitectureUnitTest.testArchitecture(Arrays.asList(ArchitectureRule.USE_ARRAYLIST_INSTEAD_OF_VECTOR, ArchitectureRule.USE_HASHMAP_INSTEAD_OF_HASHTABLE), 
        "com.ldiamond.archunittest.testpackage");
    }

    @Test void testImplViolationFailsRuleset () { // Negative test for INTERFACES_SHOULD_NOT_END_IN_IMPL
        AssertionError ae = assertThrowsExactly (AssertionError.class, () -> {
            ArchitectureUnitTest.testArchitecture("com.ldiamond.archunittest.impl.hasImplInterface");
        });
        assertTrue(ae.toString().contains("Architecture Violation [Priority: MEDIUM] - Rule 'no classes that have simple name ending with 'Impl' should be interfaces, because Interfaces should not be named Impl' was violated (1 times):"));
        assertTrue(ae.toString().contains("Class <com.ldiamond.archunittest.impl.hasImplInterface.BlahImpl> is an interface in (BlahImpl.java:0)"));
        assertEquals(371, ae.toString().length());
    }

    @Test void testNoImplInterfacesDoesNotFailRuleset () { // Negative test for INTERFACES_SHOULD_NOT_END_IN_IMPL
        ArchitectureUnitTest.testArchitecture("com.ldiamond.archunittest.impl.doesNotHaveImplInterface");
    }

    @Test void testNoInterfacesDoesNotFailRuleset () { // Positive test for INTERFACES_SHOULD_NOT_END_IN_IMPL
        ArchitectureUnitTest.testArchitecture("com.ldiamond.archunittest.impl.doesNotHaveInterfaces");
    }

    @Test void testJpaCohesionViolationFailsRuleset () { // negative test for JPA_COUPLING_RESTFUL_GET_MAPPINGS
        AssertionError ae = assertThrowsExactly (AssertionError.class, () -> {
            ArchitectureUnitTest.testArchitecture("com.ldiamond.archunittest.jparest.hasProblem");
        });
        assertTrue(ae.toString().contains("Architecture Violation [Priority: MEDIUM] - Rule 'no methods that are annotated with @GetMapping should have raw return type annotated with @Entity, because JPA_COUPLING_RESTFUL_GET_MAPPINGS REST response types should not be forced to always exactly match JPA Entity types' was violated (1 times):"));
        assertTrue(ae.toString().contains("Method <com.ldiamond.archunittest.jparest.hasProblem.ProblemController.failmytest(java.lang.String)> has raw return type annotated with @Entity in (ProblemController.java:29)"));
        assertEquals(549, ae.toString().length());
    }

    @Test void testJpaCohesionViolationDoesNotFailRuleset () { // positive test for JPA_COUPLING_RESTFUL_GET_MAPPINGS
        ArchitectureUnitTest.testArchitecture("com.ldiamond.archunittest.jparest.hasNoProblem");
    }

    @Test void testIsMethodReturnsPrimitiveBooleanFailsRuleset () { // negative test for IS_METHODS_RETURN_PRIMITIVE_BOOLEAN
        AssertionError ae = assertThrowsExactly (AssertionError.class, () -> {
            ArchitectureUnitTest.testArchitecture("com.ldiamond.archunittest.IsReturnsBoolean.IsDoesNotReturnBoolean");
        });        
        assertTrue(ae.toString().contains("Architecture Violation [Priority: MEDIUM] - Rule 'methods that have name matching 'is[A-Z][a-zA-Z]+' should have raw return type primitive boolean, because Is methods should return primitive boolean - Boolean could return null' was violated (1 times):"));
        assertTrue(ae.toString().contains("Method <com.ldiamond.archunittest.IsReturnsBoolean.IsDoesNotReturnBoolean.Blah.isBad()> does not have raw return type primitive boolean in (Blah.java:23)"));
        assertEquals(482, ae.toString().length());
    }

    @Test void testIsMethodReturnsPrimitiveBooleanDoesNotFailRuleset () { // positive test for IS_METHODS_RETURN_PRIMITIVE_BOOLEAN
        ArchitectureUnitTest.testArchitecture("com.ldiamond.archunittest.IsReturnsBoolean.IsDoesReturnBoolean");
    }

    @Test void testRestateNegativeIsNotFailsRuleset () { // negative test for RESTATE_ISNOT_METHODS_AS_POSITIVE
        AssertionError ae = assertThrowsExactly (AssertionError.class, () -> {
            ArchitectureUnitTest.testArchitecture("com.ldiamond.archunittest.restatePositive.hasNegative");
        });
        assertTrue(ae.toString().contains("Architecture Violation [Priority: MEDIUM] - Rule 'no methods that have name matching 'isNot[A-Z][a-zA-Z]+' should have name starting with 'isNot', because isNot methods should be rewritten as positive conditions since if (!isNotSomething() is confusing' was violated (1 times):"));
        assertTrue(ae.toString().contains("Method <com.ldiamond.archunittest.restatePositive.hasNegative.Blah.isNotBlah()> has name starting with 'isNot' in (Blah.java:23)"));
        assertEquals(483, ae.toString().length());
    }

    @Test void testRestateNegativeIsNotDoesNotFailRuleset () { // positive test for RESTATE_ISNOT_METHODS_AS_POSITIVE
        ArchitectureUnitTest.testArchitecture("com.ldiamond.archunittest.restatePositive.hasPositive");
    }

    @Test void testControllerCallingServiceCallingRepositoryIsGood() {  // positive test for SPRING_BOOT_SERVICES_SHOULD_NOT_CALL_CONTROLLER_METHODS SPRING_BOOT_REPOSITORIES_SHOULD_NOT_CALL_CONTROLLER_METHODS SPRING_BOOT_REPOSITORIES_SHOULD_NOT_CALL_SERVICE_METHODS
        ArchitectureUnitTest.testArchitecture("com.ldiamond.archunittest.springdependencies.allgood");
    }

    @Test void testServiceCallingControllerIsBad () {  // negative test for SPRING_BOOT_SERVICES_SHOULD_NOT_CALL_CONTROLLER_METHODS
        AssertionError ae = assertThrowsExactly (AssertionError.class, () -> {
            ArchitectureUnitTest.testArchitecture("com.ldiamond.archunittest.springdependencies.servicedependsoncontroller");
        });
        assertTrue(ae.toString().contains("Architecture Violation [Priority: MEDIUM] - Rule 'no classes that are annotated with @Service should depend on classes that are annotated with @Controller, because Spring Boot Services should not call Controller methods' was violated (1 times):"));
        assertTrue(ae.toString().contains("Method <com.ldiamond.archunittest.springdependencies.servicedependsoncontroller.BadService.doSomething()> calls method <com.ldiamond.archunittest.springdependencies.servicedependsoncontroller.BadController.badIdea()> in (BadService.java:22"));
        assertEquals(562, ae.toString().length());
    }

    @Test void testRepositoryCallingControllerIsBad () {  // negative test for SPRING_BOOT_REPOSITORIES_SHOULD_NOT_CALL_CONTROLLER_METHODS
        AssertionError ae = assertThrowsExactly (AssertionError.class, () -> {
            ArchitectureUnitTest.testArchitecture("com.ldiamond.archunittest.springdependencies.repositorydependsoncontroller");
        });
        assertTrue(ae.toString().contains("Architecture Violation [Priority: MEDIUM] - Rule 'no classes that are annotated with @Repository should depend on classes that are annotated with @Controller, because Spring Boot Repositories should not call Controller methods' was violated (1 times)"));
        assertTrue(ae.toString().contains("Method <com.ldiamond.archunittest.springdependencies.repositorydependsoncontroller.BadRepository.doSomething()> calls method <com.ldiamond.archunittest.springdependencies.repositorydependsoncontroller.BadController.badIdea()> in (BadRepository.java:22"));
        assertEquals(581, ae.toString().length());
    }

    @Test void testRepositoryCallingServiceIsBad () {  // negative test for SPRING_BOOT_REPOSITORIES_SHOULD_NOT_CALL_SERVICE_METHODS
        AssertionError ae = assertThrowsExactly (AssertionError.class, () -> {
            ArchitectureUnitTest.testArchitecture("com.ldiamond.archunittest.springdependencies.repositorydependsonservice");
        });
        assertTrue(ae.toString().contains("Architecture Violation [Priority: MEDIUM] - Rule 'no classes that are annotated with @Repository should depend on classes that are annotated with @Service, because Spring Boot Repositories should not call Service methods' was violated (1 times):"));
        assertTrue(ae.toString().contains("Method <com.ldiamond.archunittest.springdependencies.repositorydependsonservice.BadRepository.doSomething()> calls method <com.ldiamond.archunittest.springdependencies.repositorydependsonservice.BadService.badIdea()> in (BadRepository.java:22"));
        assertEquals(566, ae.toString().length());
    }

    @Test void testFieldsThatArentIsBlahIsGood() { // positive test for RENAME_IS_FIELDS
        ArchitectureUnitTest.testArchitecture("com.ldiamond.archunittest.primitiveBooleanIs.good");
    }

    @Test void testFieldsThatAreIsBlahIsBad () { // negative test for RENAME_IS_FIELDS
        AssertionError ae = assertThrowsExactly (AssertionError.class, () -> {
            ArchitectureUnitTest.testArchitecture("com.ldiamond.archunittest.primitiveBooleanIs.bad");
        });
        assertTrue(ae.toString().contains("Architecture Violation [Priority: MEDIUM] - Rule 'no fields should have name matching 'is[A-Z][a-zA-Z]+', because isBlah should be rewritten as blah, getter on isBlah would be isIsBlah which is confusing' was violated (2 times):"));
        assertTrue(ae.toString().contains("Field <com.ldiamond.archunittest.primitiveBooleanIs.bad.BadFields.isAlsoNotAGoodChoice> has name matching 'is[A-Z][a-zA-Z]+' in (BadFields.java:0)"));
        assertTrue(ae.toString().contains("Field <com.ldiamond.archunittest.primitiveBooleanIs.bad.BadFields.isNotAGoodIdea> has name matching 'is[A-Z][a-zA-Z]+' in (BadFields.java:0)"));
        assertEquals(594, ae.toString().length());
    }

    @Test void testMethodAnnotatedWithGetMappingIsInControllerClass() { // positive test for SPRING_BOOT_REQUESTMAPPING_CONTROLLER
        ArchitectureUnitTest.testArchitecture("com.ldiamond.archunittest.springannotations.good");
    }

    @Test void testMethodAnnotatedWithGetMappingIsntInControllerClassFailsRuleset() { // negative test for SPRING_BOOT_REQUESTMAPPING_CONTROLLER
        AssertionError ae = assertThrowsExactly (AssertionError.class, () -> {
            ArchitectureUnitTest.testArchitecture("com.ldiamond.archunittest.springannotations.bad");
        });
        assertTrue(ae.toString().contains("Architecture Violation [Priority: MEDIUM] - Rule 'methods that are annotated with @GetMapping should be declared in classes that are annotated with @RestController or should be declared in classes that are annotated with @Controller, because SPRING_BOOT_GETMAPPING_CONTROLLER GetMapping methods should be in Controller classes' was violated (1 times):"));
        assertTrue(ae.toString().contains("Method <com.ldiamond.archunittest.springannotations.bad.BadService.passmytest(java.lang.String)> is not declared in classes that are annotated with @Controller in (BadService.java:10) and Method <com.ldiamond.archunittest.springannotations.bad.BadService.passmytest(java.lang.String)> is not declared in classes that are annotated with @RestController in (BadService.java:10)"));
        assertEquals(804, ae.toString().length());
    }

    @Test void testStaticUnitTestsFail() { // negative test for STATIC_UNIT_TESTS_SHOULD_NOT_BE_STATIC and STATIC_UNIT_TESTS_SHOULD_NOT_BE_PRIVATE
        AssertionError ae = assertThrowsExactly (AssertionError.class, () -> {
            ArchitectureUnitTest.testArchitecture("com.ldiamond.archunittest.invalidjunittests");
        });
        assertTrue(ae.toString().contains("Architecture Violation [Priority: MEDIUM] - Rule 'no methods that are annotated with @Test should be static, because JUNIT5_TESTS_CANT_BE_STATIC JUnit 5 ignores tests that are static' was violated (1 times):"));
        assertTrue(ae.toString().contains("Method <com.ldiamond.archunittest.invalidjunittests.StaticUnitTest.testNoImplInterfacesDoesNotFailRuleset()> has modifier STATIC in (StaticUnitTest.java:9)"));

        assertTrue(ae.toString().contains("Architecture Violation [Priority: MEDIUM] - Rule 'no methods that are annotated with @Test should be private or should not be protected, because JUNIT5_TESTS_CANT_BE_PRIVATE JUnit 5 ignores tests that are private' was violated (3 times):"));
        assertTrue(ae.toString().contains("Method <com.ldiamond.archunittest.invalidjunittests.PrivateUnitTest.testNoImplInterfacesDoesNotFailRuleset()> does not have modifier PROTECTED in (PrivateUnitTest.java:9)"));
        assertTrue(ae.toString().contains("Method <com.ldiamond.archunittest.invalidjunittests.PrivateUnitTest.testNoImplInterfacesDoesNotFailRuleset()> has modifier PRIVATE in (PrivateUnitTest.java:9)"));
        assertTrue(ae.toString().contains("Method <com.ldiamond.archunittest.invalidjunittests.StaticUnitTest.testNoImplInterfacesDoesNotFailRuleset()> does not have modifier PROTECTED in (StaticUnitTest.java:9)"));

        assertEquals(1181, ae.toString().length());
    }
}
