package com.ldiamond.archunittest;

import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class TestRuleTree {

    private static final List<ArchitectureRule> ALL = Arrays.asList(ArchitectureRule.values());

    /**
     * Collects every rule leaf together with the ids of the group nodes above it (root excluded).
     */
    private static void leaves(RuleTreeNode node, List<String> path, Map<ArchitectureRule, List<List<String>>> out) {
        if (node.getKind() == RuleTreeNode.Kind.RULE) {
            ArchitectureRule rule = node.getRule().orElseThrow();
            out.computeIfAbsent(rule, r -> new ArrayList<>()).add(new ArrayList<>(path));
            return;
        }
        for (RuleTreeNode child : node.getChildren()) {
            List<String> next = new ArrayList<>(path);
            if (child.getKind() == RuleTreeNode.Kind.GROUP) {
                next.add(child.getId());
            }
            leaves(child, next, out);
        }
    }

    private static Map<ArchitectureRule, List<List<String>>> leaves(RuleTreeNode root) {
        Map<ArchitectureRule, List<List<String>>> out = new HashMap<>();
        leaves(root, new ArrayList<>(), out);
        return out;
    }

    private static void assertNoEmptyGroups(RuleTreeNode node) {
        if (node.getKind() == RuleTreeNode.Kind.RULE) {
            assertTrue(node.getChildren().isEmpty(), node.getId());
            assertEquals(1, node.getRuleCount(), node.getId());
            return;
        }
        assertFalse(node.getChildren().isEmpty(), "Empty group " + node.getId());
        assertTrue(node.getRule().isEmpty(), node.getId());
        assertEquals(node.getChildren().stream().mapToInt(RuleTreeNode::getRuleCount).sum(), node.getRuleCount(), node.getId());
        node.getChildren().forEach(TestRuleTree::assertNoEmptyGroups);
    }

    @Test
    void everyRuleDeclaresMetadata() {
        for (ArchitectureRule rule : ArchitectureRule.values()) {
            assertNotNull(rule.getCategory(), rule.name());
            assertTrue(rule.getSince().matches("\\d+\\.\\d+\\.\\d+"), rule.name() + " since " + rule.getSince());
            assertFalse(rule.getTags().isEmpty(), rule.name() + " has no tags");
        }
    }

    @Test
    void tagsAreImmutable() {
        assertThrows(UnsupportedOperationException.class, () -> ArchitectureRule.USE_ARRAYLIST_INSTEAD_OF_VECTOR.getTags().add(RuleTag.SPRING));
        assertEquals(List.of(RuleTag.PERFORMANCE, RuleTag.CONCURRENCY, RuleTag.COLLECTIONS),
                new ArrayList<>(ArchitectureRule.USE_ARRAYLIST_INSTEAD_OF_VECTOR.getTags()));
    }

    @Test
    void categoryPathRunsFromRootToSelf() {
        assertEquals(List.of(RuleCategory.SPRING, RuleCategory.SPRING_LAYERING, RuleCategory.SPRING_LAYERING_BY_PACKAGE),
                RuleCategory.SPRING_LAYERING_BY_PACKAGE.getPath());
        assertEquals(List.of(RuleCategory.SPRING), RuleCategory.SPRING.getPath());
        assertTrue(RuleCategory.SPRING.getParent().isEmpty());
        assertEquals(RuleCategory.SPRING, RuleCategory.SPRING_WEB.getParent().orElseThrow());
        for (RuleCategory c : RuleCategory.values()) {
            c.getParent().ifPresent(p -> assertTrue(p.ordinal() < c.ordinal(), c + " declared before its parent " + p));
            assertFalse(c.getLabel().isBlank(), c.name());
            assertFalse(c.getDescription().isBlank(), c.name());
        }
    }

    @Test
    void everyCategoryAndTagIsUsed() {
        for (RuleCategory c : RuleCategory.values()) {
            assertTrue(ALL.stream().anyMatch(r -> r.getCategory().getPath().contains(c)), "Unused category " + c);
        }
        for (RuleTag t : RuleTag.values()) {
            assertTrue(ALL.stream().anyMatch(r -> r.getTags().contains(t)), "Unused tag " + t);
            assertFalse(t.getLabel().isBlank(), t.name());
        }
    }

    @Test
    void categoryTreeContainsEveryRuleExactlyOnceUnderItsCategoryPath() {
        RuleTreeNode root = RuleTree.byCategory(ALL);
        assertEquals(RuleTreeNode.Kind.ROOT, root.getKind());
        assertEquals("category", root.getId());
        assertEquals(RuleTree.ROOT_LABEL, root.getLabel());
        assertEquals(ArchitectureRule.values().length, root.getRuleCount());
        assertNoEmptyGroups(root);

        Map<ArchitectureRule, List<List<String>>> leaves = leaves(root);
        assertEquals(ArchitectureRule.values().length, leaves.size());
        for (ArchitectureRule rule : ArchitectureRule.values()) {
            List<String> expected = rule.getCategory().getPath().stream().map(Enum::name).collect(Collectors.toList());
            assertEquals(List.of(expected), leaves.get(rule), rule.name());
        }
    }

    @Test
    void categoryTreeKeepsDeclarationOrder() {
        RuleTreeNode root = RuleTree.byCategory(ALL);
        List<String> top = root.getChildren().stream().map(RuleTreeNode::getId).collect(Collectors.toList());
        assertEquals(List.of("GENERAL_CODING", "PERFORMANCE", "SPRING", "PERSISTENCE", "TESTING", "THIRD_PARTY_LIBRARIES"), top);
        RuleTreeNode general = root.getChildren().get(0);
        assertEquals("General coding practices", general.getLabel());
        assertEquals(RuleCategory.GENERAL_CODING.getDescription(), general.getDescription());
        RuleTreeNode archunit = general.getChildren().get(0);
        assertEquals("ARCHUNIT_GENERAL_CODING_RULES", archunit.getId());
        assertEquals("ARCHUNIT_ASSERTIONS_SHOULD_HAVE_DETAIL_MESSAGE", archunit.getChildren().get(0).getId());
    }

    @Test
    void subsetOnlyBuildsTheBranchesItNeeds() {
        RuleTreeNode root = RuleTree.byCategory(List.of(ArchitectureRule.SPRING_BOOT_SERVICES_SHOULD_NOT_CALL_CONTROLLERS));
        assertEquals(1, root.getRuleCount());
        RuleTreeNode spring = root.getChildren().get(0);
        assertEquals(1, root.getChildren().size());
        assertEquals("SPRING", spring.getId());
        RuleTreeNode layering = spring.getChildren().get(0);
        assertEquals(1, spring.getChildren().size());
        assertEquals("SPRING_LAYERING", layering.getId());
        RuleTreeNode byPackage = layering.getChildren().get(0);
        assertEquals(1, layering.getChildren().size());
        assertEquals("SPRING_LAYERING_BY_PACKAGE", byPackage.getId());
        RuleTreeNode leaf = byPackage.getChildren().get(0);
        assertEquals(RuleTreeNode.Kind.RULE, leaf.getKind());
        assertEquals(ArchitectureRule.SPRING_BOOT_SERVICES_SHOULD_NOT_CALL_CONTROLLERS, leaf.getRule().orElseThrow());
        assertEquals(ArchitectureRule.SPRING_BOOT_SERVICES_SHOULD_NOT_CALL_CONTROLLERS.getRule().getDescription(), leaf.getDescription());
    }

    @Test
    void inputOrderAndDuplicatesDoNotMatter() {
        List<ArchitectureRule> shuffled = new ArrayList<>(ALL);
        java.util.Collections.reverse(shuffled);
        shuffled.addAll(ALL);
        RuleTreeNode a = RuleTree.byCategory(ALL);
        RuleTreeNode b = RuleTree.byCategory(shuffled);
        assertEquals(leaves(a), leaves(b));
        assertEquals(a.getRuleCount(), b.getRuleCount());
        assertEquals(RuleTree.bySince(ALL).getRuleCount(), RuleTree.bySince(shuffled).getRuleCount());
        assertEquals(RuleTree.byTag(ALL).getRuleCount(), RuleTree.byTag(shuffled).getRuleCount());
    }

    @Test
    void tagTreeListsEachRuleUnderEachOfItsTags() {
        RuleTreeNode root = RuleTree.byTag(ALL);
        assertEquals("tag", root.getId());
        assertNoEmptyGroups(root);
        assertEquals(ALL.stream().mapToInt(r -> r.getTags().size()).sum(), root.getRuleCount());
        Map<ArchitectureRule, List<List<String>>> leaves = leaves(root);
        for (ArchitectureRule rule : ArchitectureRule.values()) {
            List<List<String>> expected = rule.getTags().stream().map(t -> List.of(t.name())).collect(Collectors.toList());
            assertEquals(expected, leaves.get(rule), rule.name());
        }
        assertEquals("ArchUnit library", root.getChildren().get(0).getLabel());
    }

    @Test
    void sinceTreeIsNewestFirst() {
        RuleTreeNode root = RuleTree.bySince(ALL);
        assertEquals("since", root.getId());
        assertNoEmptyGroups(root);
        assertEquals(ArchitectureRule.values().length, root.getRuleCount());
        List<String> versions = root.getChildren().stream().map(RuleTreeNode::getId).collect(Collectors.toList());
        assertEquals(List.of("5.1.0", "5.0.0", "4.0.2", "4.0.0", "2.0.0", "1.0.0"), versions);
        assertEquals("Since 5.1.0", root.getChildren().get(0).getLabel());
        leaves(root).forEach((rule, paths) -> assertEquals(List.of(List.of(rule.getSince())), paths, rule.name()));
    }

    @Test
    void versionsCompareNumerically() {
        assertTrue(RuleTree.VERSION_ORDER.compare("10.0.0", "9.0.0") > 0);
        assertTrue(RuleTree.VERSION_ORDER.compare("4.0.2", "4.0") > 0);
        assertTrue(RuleTree.VERSION_ORDER.compare("4.0", "4.0.2") < 0);
        assertEquals(0, RuleTree.VERSION_ORDER.compare("1.0", "1.0.0"));
        assertEquals(0, RuleTree.VERSION_ORDER.compare("5.1.0", "5.1.0"));
    }

    @Test
    void emptyInputGivesEmptyTrees() {
        assertEquals(0, RuleTree.byCategory(List.of()).getRuleCount());
        assertTrue(RuleTree.byCategory(List.of()).getChildren().isEmpty());
        assertTrue(RuleTree.byTag(List.of()).getChildren().isEmpty());
        assertTrue(RuleTree.bySince(List.of()).getChildren().isEmpty());
    }
}
