package com.ldiamond.archunittest;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Builds navigable trees of {@link ArchitectureRule}s from the metadata declared on each rule.
 * <p>
 * Nothing here is maintained by hand: adding a rule (with its {@link RuleCategory}, version and {@link RuleTag}s)
 * is enough for it to appear in every view. Groups that would be empty are omitted.
 */
public final class RuleTree {

    /**
     * Title used for the root node of every view.
     */
    static final String ROOT_LABEL = "Architecture rules";

    private RuleTree() {
    }

    /**
     * The primary view: rules nested under their category hierarchy
     *
     * @param rules the rules to include, usually {@code Arrays.asList(ArchitectureRule.values())}
     * @return root node; each rule appears exactly once
     */
    public static RuleTreeNode byCategory(final Collection<ArchitectureRule> rules) {
        Set<RuleCategory> used = EnumSet.noneOf(RuleCategory.class);
        rules.forEach(r -> used.addAll(r.getCategory().getPath()));
        List<RuleTreeNode> top = Arrays.stream(RuleCategory.values())
                .filter(c -> c.getParent().isEmpty() && used.contains(c))
                .map(c -> categoryNode(c, used, rules))
                .collect(Collectors.toList());
        return RuleTreeNode.root("category", ROOT_LABEL, "Grouped by category", top);
    }

    private static RuleTreeNode categoryNode(final RuleCategory category, final Set<RuleCategory> used, final Collection<ArchitectureRule> rules) {
        List<RuleTreeNode> children = new ArrayList<>();
        Arrays.stream(RuleCategory.values())
                .filter(c -> c.getParent().filter(category::equals).isPresent() && used.contains(c))
                .forEach(c -> children.add(categoryNode(c, used, rules)));
        sorted(rules).stream()
                .filter(r -> r.getCategory() == category)
                .forEach(r -> children.add(RuleTreeNode.leaf(r)));
        return RuleTreeNode.group(category.name(), category.getLabel(), category.getDescription(), children);
    }

    /**
     * A cross-cutting view: one group per tag. A rule with several tags appears under each of them.
     *
     * @param rules the rules to include
     * @return root node
     */
    public static RuleTreeNode byTag(final Collection<ArchitectureRule> rules) {
        List<RuleTreeNode> groups = new ArrayList<>();
        for (RuleTag tag : RuleTag.values()) {
            List<RuleTreeNode> leaves = sorted(rules).stream()
                    .filter(r -> r.getTags().contains(tag))
                    .map(RuleTreeNode::leaf)
                    .collect(Collectors.toList());
            if (!leaves.isEmpty()) {
                groups.add(RuleTreeNode.group(tag.name(), tag.getLabel(), "", leaves));
            }
        }
        return RuleTreeNode.root("tag", ROOT_LABEL, "Grouped by tag", groups);
    }

    /**
     * A history view: one group per library version that introduced rules, newest first
     *
     * @param rules the rules to include
     * @return root node; each rule appears exactly once
     */
    public static RuleTreeNode bySince(final Collection<ArchitectureRule> rules) {
        TreeMap<String, List<RuleTreeNode>> byVersion = new TreeMap<>(VERSION_ORDER.reversed());
        sorted(rules).forEach(r -> byVersion.computeIfAbsent(r.getSince(), v -> new ArrayList<>()).add(RuleTreeNode.leaf(r)));
        List<RuleTreeNode> groups = byVersion.entrySet().stream()
                .map(e -> RuleTreeNode.group(e.getKey(), "Since " + e.getKey(), "", e.getValue()))
                .collect(Collectors.toList());
        return RuleTreeNode.root("since", ROOT_LABEL, "Grouped by the version that introduced them", groups);
    }

    /**
     * Orders dotted version strings numerically, so that 10.0.0 sorts after 9.0.0.
     */
    static final Comparator<String> VERSION_ORDER = (a, b) -> {
        String[] x = a.split("\\.");
        String[] y = b.split("\\.");
        for (int i = 0; i < Math.max(x.length, y.length); i++) {
            int cmp = Integer.compare(part(x, i), part(y, i));
            if (cmp != 0) {
                return cmp;
            }
        }
        return 0;
    };

    private static int part(final String[] parts, final int i) {
        return i < parts.length ? Integer.parseInt(parts[i]) : 0;
    }

    /**
     * Rules in enum declaration order, regardless of the order of the input collection.
     */
    private static List<ArchitectureRule> sorted(final Collection<ArchitectureRule> rules) {
        return rules.stream().distinct().sorted().collect(Collectors.toList());
    }
}
