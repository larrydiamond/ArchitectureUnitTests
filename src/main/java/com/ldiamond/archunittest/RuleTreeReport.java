package com.ldiamond.archunittest;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Renders the rules trees built by {@link RuleTree} as a self-contained, navigable HTML page and as JSON.
 * <p>
 * The page uses progressive disclosure: top-level categories are shown collapsed with rule counts, and each level
 * (sub-category, rule, rule details) opens on demand. It can regroup the same rules by category, tag or version,
 * filter by text and tag, and deep-link to a rule with {@code #RULE_NAME}. It has no external dependencies.
 * <p>
 * Run {@code ./gradlew rulesTree} to generate {@code build/reports/rules-tree/index.html} and {@code rules-tree.json}.
 */
public final class RuleTreeReport {

    static final String HTML_FILE = "index.html";
    static final String JSON_FILE = "rules-tree.json";
    static final String TEMPLATE = "rules-tree-template.html";
    private static final String BECAUSE = ", because ";

    private RuleTreeReport() {
    }

    /**
     * Command line entry point used by the {@code rulesTree} Gradle task
     *
     * @param args optional output directory (default {@code build/reports/rules-tree}) and optional version label
     * @throws IOException if the report cannot be written
     */
    public static void main(final String[] args) throws IOException {
        Path dir = Paths.get(args.length > 0 ? args[0] : "build/reports/rules-tree");
        String version = args.length > 1 ? args[1] : "";
        writeTo(dir, Arrays.asList(ArchitectureRule.values()), version);
    }

    /**
     * Writes {@code index.html} and {@code rules-tree.json} into a directory, creating it if needed
     *
     * @param dir     output directory
     * @param rules   rules to include
     * @param version library version shown in the page header, may be empty
     * @throws IOException if the files cannot be written
     */
    public static void writeTo(final Path dir, final Collection<ArchitectureRule> rules, final String version) throws IOException {
        Files.createDirectories(dir);
        Files.write(dir.resolve(HTML_FILE), toHtml(rules, version).getBytes(StandardCharsets.UTF_8));
        Files.write(dir.resolve(JSON_FILE), toJson(rules).getBytes(StandardCharsets.UTF_8));
    }

    /**
     * The views offered by the report, keyed by view id, in display order.
     */
    static Map<String, RuleTreeNode> views(final Collection<ArchitectureRule> rules) {
        Map<String, RuleTreeNode> views = new LinkedHashMap<>();
        views.put("category", RuleTree.byCategory(rules));
        views.put("tag", RuleTree.byTag(rules));
        views.put("since", RuleTree.bySince(rules));
        return views;
    }

    // ------------------------------------------------------------------ HTML

    /**
     * Renders the self-contained HTML page
     *
     * @param rules   rules to include
     * @param version library version shown in the page header, may be empty
     * @return HTML document
     */
    public static String toHtml(final Collection<ArchitectureRule> rules, final String version) {
        Map<String, RuleTreeNode> views = views(rules);
        StringBuilder body = new StringBuilder();
        views.forEach((id, root) -> {
            body.append("<section class=\"view\" data-view=\"").append(id).append("\" aria-label=\"")
                    .append(html(root.getDescription())).append("\">\n");
            root.getChildren().forEach(child -> renderNode(body, child, 1));
            body.append("</section>\n");
        });
        StringBuilder tags = new StringBuilder();
        Arrays.stream(RuleTag.values())
                .filter(t -> rules.stream().anyMatch(r -> r.getTags().contains(t)))
                .forEach(t -> tags.append(tagButton(t)));
        long categories = Arrays.stream(RuleCategory.values())
                .filter(c -> rules.stream().anyMatch(r -> r.getCategory().getPath().contains(c))).count();
        String summary = views.get("category").getRuleCount() + " rules &middot; " + categories + " categories &middot; "
                + views.get("tag").getChildren().size() + " tags";
        return template()
                .replace("{{VERSION}}", version.isEmpty() ? "" : " " + html(version))
                .replace("{{SUMMARY}}", summary)
                .replace("{{TAGS}}", tags.toString())
                .replace("{{VIEWS}}", body.toString());
    }

    private static void renderNode(final StringBuilder sb, final RuleTreeNode node, final int depth) {
        if (node.getRule().isPresent()) {
            renderRule(sb, node.getRule().get());
            return;
        }
        sb.append("<details class=\"group\" data-depth=\"").append(depth).append("\">")
                .append("<summary><span class=\"label\">").append(html(node.getLabel())).append("</span>")
                .append("<span class=\"count\" title=\"rules\">").append(node.getRuleCount()).append("</span>");
        if (!node.getDescription().isEmpty()) {
            sb.append("<span class=\"desc\">").append(html(node.getDescription())).append("</span>");
        }
        sb.append("</summary>\n<div class=\"children\">\n");
        node.getChildren().forEach(child -> renderNode(sb, child, depth + 1));
        sb.append("</div></details>\n");
    }

    private static void renderRule(final StringBuilder sb, final ArchitectureRule rule) {
        String what = what(rule.getRule().getDescription());
        String why = why(rule.getRule().getDescription());
        String path = rule.getCategory().getPath().stream().map(RuleCategory::getLabel).collect(Collectors.joining(" › "));
        String tagIds = rule.getTags().stream().map(Enum::name).collect(Collectors.joining(" "));

        sb.append("<details class=\"rule\" data-rule=\"").append(rule.name()).append("\" data-tags=\"").append(tagIds).append("\">")
                .append("<summary><code class=\"name\">").append(rule.name()).append("</code>")
                .append("<span class=\"what\">").append(html(what)).append("</span></summary>\n")
                .append("<dl class=\"facts\">")
                .append("<dt>Checks</dt><dd>").append(html(what)).append("</dd>");
        if (!why.isEmpty()) {
            sb.append("<dt>Why</dt><dd>").append(html(why)).append("</dd>");
        }
        sb.append("<dt>Category</dt><dd>").append(html(path)).append("</dd>")
                .append("<dt>Since</dt><dd>").append(html(rule.getSince())).append("</dd>")
                .append("<dt>Tags</dt><dd class=\"tags\">");
        rule.getTags().forEach(t -> sb.append(tagButton(t)));
        sb.append("</dd><dt>Exclude</dt><dd><pre class=\"snippet\"><code>")
                .append(html(exclusionSnippet(rule)))
                .append("</code></pre><button type=\"button\" class=\"copy\">Copy</button> <a class=\"permalink\" href=\"#")
                .append(rule.name()).append("\">Link to this rule</a></dd></dl></details>\n");
    }

    private static String tagButton(final RuleTag tag) {
        return "<button type=\"button\" class=\"tag\" data-tag=\"" + tag.name() + "\">" + html(tag.getLabel()) + "</button>";
    }

    /**
     * The condition part of the ArchUnit description, without the {@code because} clause.
     */
    static String what(final String description) {
        int i = description.indexOf(BECAUSE);
        return i < 0 ? description : description.substring(0, i);
    }

    /**
     * The {@code because} clause of the ArchUnit description, or empty.
     */
    static String why(final String description) {
        int i = description.indexOf(BECAUSE);
        return i < 0 ? "" : description.substring(i + BECAUSE.length());
    }

    static String exclusionSnippet(final ArchitectureRule rule) {
        return "ArchitectureUnitTest.testArchitecture(\n    List.of(ArchitectureRule." + rule.name() + "),\n    \"com.example.myproject\");";
    }

    /**
     * Escapes text for use in HTML element content and double- or single-quoted attributes.
     */
    static String html(final String text) {
        StringBuilder sb = new StringBuilder(text.length());
        for (char c : text.toCharArray()) {
            switch (c) {
                case '&':
                    sb.append("&amp;");
                    break;
                case '<':
                    sb.append("&lt;");
                    break;
                case '>':
                    sb.append("&gt;");
                    break;
                case '"':
                    sb.append("&quot;");
                    break;
                case '\'':
                    sb.append("&#39;");
                    break;
                default:
                    sb.append(c);
            }
        }
        return sb.toString();
    }

    private static String template() {
        try (InputStream in = RuleTreeReport.class.getResourceAsStream(TEMPLATE)) {
            if (in == null) {
                throw new IllegalStateException("Missing resource " + TEMPLATE);
            }
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    // ------------------------------------------------------------------ JSON

    /**
     * Renders every view as JSON: {@code {"ruleCount": n, "views": {"category": node, "tag": node, "since": node}}}
     *
     * @param rules rules to include
     * @return JSON document
     */
    public static String toJson(final Collection<ArchitectureRule> rules) {
        Map<String, RuleTreeNode> views = views(rules);
        StringBuilder sb = new StringBuilder("{\n  \"ruleCount\": ").append(views.get("category").getRuleCount())
                .append(",\n  \"views\": {");
        String sep = "\n    ";
        for (Map.Entry<String, RuleTreeNode> e : views.entrySet()) {
            sb.append(sep).append(json(e.getKey())).append(": ");
            renderJson(sb, e.getValue());
            sep = ",\n    ";
        }
        return sb.append("\n  }\n}\n").toString();
    }

    private static void renderJson(final StringBuilder sb, final RuleTreeNode node) {
        sb.append("{\"kind\":").append(json(node.getKind().name().toLowerCase(Locale.ROOT)))
                .append(",\"id\":").append(json(node.getId()))
                .append(",\"label\":").append(json(node.getLabel()))
                .append(",\"description\":").append(json(node.getDescription()))
                .append(",\"ruleCount\":").append(node.getRuleCount());
        if (node.getRule().isPresent()) {
            ArchitectureRule rule = node.getRule().get();
            sb.append(",\"category\":").append(json(rule.getCategory().name()))
                    .append(",\"categoryPath\":[")
                    .append(rule.getCategory().getPath().stream().map(c -> json(c.name())).collect(Collectors.joining(",")))
                    .append("],\"since\":").append(json(rule.getSince()))
                    .append(",\"tags\":[")
                    .append(rule.getTags().stream().map(t -> json(t.name())).collect(Collectors.joining(",")))
                    .append("]}");
            return;
        }
        sb.append(",\"children\":[");
        String sep = "";
        for (RuleTreeNode child : node.getChildren()) {
            sb.append(sep);
            renderJson(sb, child);
            sep = ",";
        }
        sb.append("]}");
    }

    /**
     * Quotes and escapes a JSON string. {@code <} is escaped too so the output is safe to inline in HTML.
     */
    static String json(final String text) {
        StringBuilder sb = new StringBuilder(text.length() + 2).append('"');
        for (char c : text.toCharArray()) {
            switch (c) {
                case '"':
                    sb.append("\\\"");
                    break;
                case '\\':
                    sb.append("\\\\");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                case '<':
                    sb.append("\\u003c");
                    break;
                default:
                    if (c < 0x20) {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
            }
        }
        return sb.append('"').toString();
    }
}
