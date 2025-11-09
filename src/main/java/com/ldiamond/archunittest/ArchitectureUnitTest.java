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

import java.util.List;

import java.util.Collection;
import java.util.ArrayList;

import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.domain.JavaClasses;

/**
 * This class is the main class for this library and only exposes static methods for projects to call.
 */
public class ArchitectureUnitTest {

    private ArchitectureUnitTest() { }

    /**
     * This method analyzes all the classes in the packages listed against all of the architecture rules in the ArchitectureRule enum except the explicitly excluded rules.
     * 
     * @param exclusions A Collection of rules to exclude from the test
     * @param packagesToImport The series of packages to analyze
     */
    public static void testArchitecture (final Collection<ArchitectureRule> exclusions, final String... packagesToImport) {
        JavaClasses classes = new ClassFileImporter().importPackages(packagesToImport);

        List<AssertionError> failures = new ArrayList<>();

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
            failures.stream()
                .map(ae -> ae.getMessage() + "\n\n")
                .forEach(sb::append);
            throw new AssertionError(sb.toString());
        }
    }

    /**
     * This method analyzes all the classes in the packages listed against all of the architecture rules in the ArchitectureRule enum
     *
     * @param packagesToImport The series of packages to analyze
     */
    public static void testArchitecture (final String... packagesToImport) {
        testArchitecture (new ArrayList<>(), packagesToImport);
    }

}
