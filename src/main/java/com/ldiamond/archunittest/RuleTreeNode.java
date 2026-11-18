package com.ldiamond.archunittest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Immutable node of a rules tree built by {@link RuleTree}.
 * <p>
 * Group nodes ({@link Kind#ROOT}, {@link Kind#GROUP}) carry children; {@link Kind#RULE} nodes are leaves
 * that point at the {@link ArchitectureRule} they represent.
 */
public final class RuleTreeNode {

    /**
     * The role a node plays in the tree.
     */
    public enum Kind {
        /**
         * The single top node of a tree.
         */
        ROOT,
        /**
         * An intermediate grouping node: a category, a tag or a version.
         */
        GROUP,
        /**
         * A leaf representing a single rule.
         */
        RULE
    }

    private final Kind kind;
    private final String id;
    private final String label;
    private final String description;
    private final List<RuleTreeNode> children;
    private final ArchitectureRule rule;
    private final int ruleCount;

    private RuleTreeNode(final Kind kind, final String id, final String label, final String description,
                         final List<RuleTreeNode> children, final ArchitectureRule rule) {
        this.kind = kind;
        this.id = id;
        this.label = label;
        this.description = description;
        this.children = Collections.unmodifiableList(new ArrayList<>(children));
        this.rule = rule;
        this.ruleCount = rule != null ? 1 : this.children.stream().mapToInt(RuleTreeNode::getRuleCount).sum();
    }

    static RuleTreeNode root(final String id, final String label, final String description, final List<RuleTreeNode> children) {
        return new RuleTreeNode(Kind.ROOT, id, label, description, children, null);
    }

    static RuleTreeNode group(final String id, final String label, final String description, final List<RuleTreeNode> children) {
        return new RuleTreeNode(Kind.GROUP, id, label, description, children, null);
    }

    static RuleTreeNode leaf(final ArchitectureRule rule) {
        return new RuleTreeNode(Kind.RULE, rule.name(), rule.name(), rule.getRule().getDescription(), List.of(), rule);
    }

    /**
     * Node role
     *
     * @return kind
     */
    public Kind getKind() {
        return kind;
    }

    /**
     * Identifier, unique among siblings: the enum constant name for categories, tags and rules, the version for versions
     *
     * @return id
     */
    public String getId() {
        return id;
    }

    /**
     * Human readable name
     *
     * @return label
     */
    public String getLabel() {
        return label;
    }

    /**
     * Explanation of the node; for rules this is the ArchUnit rule description
     *
     * @return description, possibly empty
     */
    public String getDescription() {
        return description;
    }

    /**
     * Child nodes in display order
     *
     * @return unmodifiable list, empty for rules
     */
    public List<RuleTreeNode> getChildren() {
        return children;
    }

    /**
     * The rule a leaf represents
     *
     * @return rule, or empty for group nodes
     */
    public Optional<ArchitectureRule> getRule() {
        return Optional.ofNullable(rule);
    }

    /**
     * Number of rule leaves at or below this node
     *
     * @return count
     */
    public int getRuleCount() {
        return ruleCount;
    }
}
