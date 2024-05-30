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
        assertThrowsExactly (AssertionError.class, () -> {
            ArchitectureUnitTest.testArchitecture("com.ldiamond.archunittest.testpackage");
        });
    }

    @Test void testpackageFailsRulesetWithExclusionForVectorButNotHashtable () {
        assertThrowsExactly (AssertionError.class, () -> {
            ArchitectureUnitTest.testArchitecture(Arrays.asList(ArchitectureRule.USE_ARRAYLIST_INSTEAD_OF_VECTOR), 
            "com.ldiamond.archunittest.testpackage");
        });
    }

    @Test void testpackageSucceedsRulesetWithExclusionForVectorAndHashtable () {
        ArchitectureUnitTest.testArchitecture(Arrays.asList(ArchitectureRule.USE_ARRAYLIST_INSTEAD_OF_VECTOR, ArchitectureRule.USE_HASHMAP_INSTEAD_OF_HASHTABLE), 
        "com.ldiamond.archunittest.testpackage");
    }

    @Test void testImplViolationFailsRuleset () {
        assertThrowsExactly (AssertionError.class, () -> {
            ArchitectureUnitTest.testArchitecture("com.ldiamond.archunittest.impl.hasImplInterface");
        });
    }

    @Test void testNoImplInterfacesDoesNotFailRuleset () {
        ArchitectureUnitTest.testArchitecture("com.ldiamond.archunittest.impl.doesNotHaveImplInterface");
    }

    @Test void testNoInterfacesDoesNotFailRuleset () {
        ArchitectureUnitTest.testArchitecture("com.ldiamond.archunittest.impl.doesNotHaveInterfaces");
    }

    @Test void testJpaCohesionViolationFailsRuleset () {
        assertThrowsExactly (AssertionError.class, () -> {
            ArchitectureUnitTest.testArchitecture("com.ldiamond.archunittest.jparest.hasProblem");
        });
    }

    @Test void testJpaCohesionViolationDoesNotFailRuleset () {
        ArchitectureUnitTest.testArchitecture("com.ldiamond.archunittest.jparest.hasNoProblem");
    }
}
