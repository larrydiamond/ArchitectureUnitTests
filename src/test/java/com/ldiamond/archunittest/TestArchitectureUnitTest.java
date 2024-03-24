package com.ldiamond.archcommon;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import com.ldiamond.archcommon.ArchitectureRule;
import com.ldiamond.archcommon.ArchitectureUnitTest;

import static org.junit.jupiter.api.Assertions.*;

class TestArchitectureUnitTest {
    @Test void testpackageFailsRuleset () {
        assertThrowsExactly (AssertionError.class, () -> {
            ArchitectureUnitTest.testArchitecture("com.ldiamond.archcommon.testpackage");
        });
    }

    @Test void testpackageFailsRulesetWithExclusionForVectorButNotHashtable () {
        assertThrowsExactly (AssertionError.class, () -> {
            ArchitectureUnitTest.testArchitecture(Arrays.asList(ArchitectureRule.USE_ARRAYLIST_INSTEAD_OF_VECTOR), "com.ldiamond.archcommon.testpackage");
        });
    }

    @Test void testpackageSucceedsRulesetWithExclusionForVectorAndHashtable () {
        ArchitectureUnitTest.testArchitecture(Arrays.asList(ArchitectureRule.USE_ARRAYLIST_INSTEAD_OF_VECTOR, ArchitectureRule.USE_HASHMAP_INSTEAD_OF_HASHTABLE), "com.ldiamond.archcommon.testpackage");
    }
}
