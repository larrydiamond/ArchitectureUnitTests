package com.ldiamond.archunittest.invalidjunittests;

import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;

public class StaticUnitTest {

    @Test static void testNoImplInterfacesDoesNotFailRuleset () { // this test is not run, but is used to check that the static method is caught
        fail();
    }
}
