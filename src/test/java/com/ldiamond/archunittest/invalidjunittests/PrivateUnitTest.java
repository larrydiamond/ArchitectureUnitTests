package com.ldiamond.archunittest.invalidjunittests;

import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;

public class PrivateUnitTest {

    @Test private void testNoImplInterfacesDoesNotFailRuleset () { // this test is not run, but is used to check that the private method is caught
        fail();
    }
}
