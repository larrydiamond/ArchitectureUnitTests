package com.ldiamond.archunittest.validjunittests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class SomeValidTests {

    @Test void testNoImplInterfacesDoesNotFailRuleset () { // this test is run and should not set up test
        assertEquals("This test is only a test", "This test is only a test", "This test needs to succeed");
    }
}
