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

    @Test void testImplViolationFailsRuleset () {
        AssertionError ae = assertThrowsExactly (AssertionError.class, () -> {
            ArchitectureUnitTest.testArchitecture("com.ldiamond.archunittest.impl.hasImplInterface");
        });
        assertTrue(ae.toString().contains("Architecture Violation [Priority: MEDIUM] - Rule 'no classes that have simple name ending with 'Impl' should be interfaces, because Interfaces should not be named Impl' was violated (1 times):"));
        assertTrue(ae.toString().contains("Class <com.ldiamond.archunittest.impl.hasImplInterface.BlahImpl> is an interface in (BlahImpl.java:0)"));
        assertEquals(371, ae.toString().length());
    }

    @Test void testNoImplInterfacesDoesNotFailRuleset () {
        ArchitectureUnitTest.testArchitecture("com.ldiamond.archunittest.impl.doesNotHaveImplInterface");
    }

    @Test void testNoInterfacesDoesNotFailRuleset () {
        ArchitectureUnitTest.testArchitecture("com.ldiamond.archunittest.impl.doesNotHaveInterfaces");
    }

    @Test void testJpaCohesionViolationFailsRuleset () {
        AssertionError ae = assertThrowsExactly (AssertionError.class, () -> {
            ArchitectureUnitTest.testArchitecture("com.ldiamond.archunittest.jparest.hasProblem");
        });
        assertTrue(ae.toString().contains("Architecture Violation [Priority: MEDIUM] - Rule 'no methods that are annotated with @GetMapping should have raw return type annotated with @Entity, because REST response types should not be forced to always exactly match JPA Entity types' was violated (1 times):"));
        assertTrue(ae.toString().contains("Method <com.ldiamond.archunittest.jparest.hasProblem.ProblemController.failmytest(java.lang.String)> has raw return type annotated with @Entity in (ProblemController.java:29)"));
        assertEquals(515, ae.toString().length());
    }

    @Test void testJpaCohesionViolationDoesNotFailRuleset () {
        ArchitectureUnitTest.testArchitecture("com.ldiamond.archunittest.jparest.hasNoProblem");
    }

    @Test void testIsMethodReturnsPrimitiveBooleanFailsRuleset () {
        AssertionError ae = assertThrowsExactly (AssertionError.class, () -> {
            ArchitectureUnitTest.testArchitecture("com.ldiamond.archunittest.IsReturnsBoolean.IsDoesNotReturnBoolean");
        });        
        assertTrue(ae.toString().contains("Architecture Violation [Priority: MEDIUM] - Rule 'methods that have name matching 'is[A-Z][a-zA-Z]+' should have raw return type primitive boolean, because Is methods should return primitive boolean - Boolean could return null' was violated (1 times):"));
        assertTrue(ae.toString().contains("Method <com.ldiamond.archunittest.IsReturnsBoolean.IsDoesNotReturnBoolean.Blah.isBad()> does not have raw return type primitive boolean in (Blah.java:23)"));
        assertEquals(482, ae.toString().length());
    }

    @Test void testIsMethodReturnsPrimitiveBooleanDoesNotFailRuleset () {
        ArchitectureUnitTest.testArchitecture("com.ldiamond.archunittest.IsReturnsBoolean.IsDoesReturnBoolean");
    }

    @Test void testRestateNegativeIsNotFailsRuleset () {
        AssertionError ae = assertThrowsExactly (AssertionError.class, () -> {
            ArchitectureUnitTest.testArchitecture("com.ldiamond.archunittest.restatePositive.hasNegative");
        });
        assertTrue(ae.toString().contains("Architecture Violation [Priority: MEDIUM] - Rule 'no methods that have name matching 'isNot[A-Z][a-zA-Z]+' should have name starting with 'isNot', because isNot methods should be rewritten as positive conditions since if (!isNotSomething() is confusing' was violated (1 times):"));
        assertTrue(ae.toString().contains("Method <com.ldiamond.archunittest.restatePositive.hasNegative.Blah.isNotBlah()> has name starting with 'isNot' in (Blah.java:23)"));
        assertEquals(483, ae.toString().length());
    }

    @Test void testRestateNegativeIsNotDoesNotFailRuleset () {
        ArchitectureUnitTest.testArchitecture("com.ldiamond.archunittest.restatePositive.hasPositive");
    }

    @Test void testControllerCallingServiceIsGood() {
        ArchitectureUnitTest.testArchitecture("com.ldiamond.archunittest.springdependencies.allgood");
    }

    @Test void testServiceCallingControllerIsBad () {
        AssertionError ae = assertThrowsExactly (AssertionError.class, () -> {
            ArchitectureUnitTest.testArchitecture("com.ldiamond.archunittest.springdependencies.servicedependsoncontroller");
        });
        assertTrue(ae.toString().contains("Architecture Violation [Priority: MEDIUM] - Rule 'no classes that are annotated with @Service should depend on classes that are annotated with @Controller, because Spring Boot Services should not call Controller methods' was violated (1 times):"));
        assertTrue(ae.toString().contains("Method <com.ldiamond.archunittest.springdependencies.servicedependsoncontroller.BadService.doSomething()> calls method <com.ldiamond.archunittest.springdependencies.servicedependsoncontroller.BadController.badIdea()> in (BadService.java:7)"));
        assertEquals(561, ae.toString().length());
    }


}
