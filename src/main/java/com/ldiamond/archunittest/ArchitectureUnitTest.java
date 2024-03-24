package com.ldiamond.archunittest;

import java.util.Collection;
import java.util.ArrayList;

import com.ldiamond.archunittest.ArchitectureRule;

import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.domain.JavaClasses;

public class ArchitectureUnitTest {

    private ArchitectureUnitTest() { }

    public static void testArchitecture (final Collection<ArchitectureRule> exclusions, final String... packagesToImport) {
        JavaClasses classes = new ClassFileImporter().importPackages(packagesToImport);

        ArrayList<AssertionError> failures = new ArrayList<>();

        for (ArchitectureRule rule : ArchitectureRule.values()) {
            if (!exclusions.contains(rule)) {
                try {
                    rule.getRule().check(classes);
                } catch (AssertionError ae) {
                    failures.add(ae);
                }
            }
        }

        if (!failures.isEmpty()) {
            StringBuilder sb = new StringBuilder("The following architecture rules were violated:\n");
            for (AssertionError ae : failures) {
                sb.append (ae.getMessage());
                sb.append("\n\n");
            }
            throw new AssertionError(sb.toString());
        }
    }

    public static void testArchitecture (final String... packagesToImport) {
        testArchitecture (new ArrayList<>(), packagesToImport);
    }

}
