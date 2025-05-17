package com.ldiamond.archunittest.invalidjunittests;

import org.junit.jupiter.api.Test;

import com.ldiamond.archunittest.ArchitectureUnitTest;

public class StaticUnitTest {

    @Test static void testNoImplInterfacesDoesNotFailRuleset () { // this test is not run, but is used to check that the static method is caught
        ArchitectureUnitTest.testArchitecture("com.ldiamond.archunittest.impl.doesNotHaveImplInterface");
    }
}
