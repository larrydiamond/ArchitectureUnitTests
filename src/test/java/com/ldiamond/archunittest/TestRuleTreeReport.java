package com.ldiamond.archunittest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

class TestRuleTreeReport {

    private static final List<ArchitectureRule> ALL = Arrays.asList(ArchitectureRule.values());

    private static int count(String haystack, String needle) {
        int n = 0;
        for (int i = haystack.indexOf(needle); i >= 0; i = haystack.indexOf(needle, i + needle.length())) {
            n++;
        }
        return n;
    }

    @Test
    void escapesHtml() {
        assertEquals("a &lt;b&gt; &amp; &quot;c&quot; &#39;d&#39;", RuleTreeReport.html("a <b> & \"c\" 'd'"));
        assertEquals("", RuleTreeReport.html(""));
    }

    @Test
    void escapesJson() {
        assertEquals("\"q\\\"b\\\\s\\n\\r\\t\\u003c\\u0001\"", RuleTreeReport.json("q\"b\\s\n\r\t<\u0001"));
        assertEquals("\"plain >\"", RuleTreeReport.json("plain >"));
    }

    @Test
    void splitsTheBecauseClause() {
        String d = ArchitectureRule.USE_ARRAYLIST_INSTEAD_OF_VECTOR.getRule().getDescription();
        assertEquals("no classes should call constructor Vector.<init>()", RuleTreeReport.what(d));
        assertEquals("Use ArrayList instead of Vector because Arraylist is unsynchronized and faster", RuleTreeReport.why(d));
        assertEquals("no reason given", RuleTreeReport.what("no reason given"));
        assertEquals("", RuleTreeReport.why("no reason given"));
    }

    @Test
    void exclusionSnippetNamesTheRule() {
        assertEquals("ArchitectureUnitTest.testArchitecture(\n    List.of(ArchitectureRule.RENAME_IS_FIELDS),\n    \"com.example.myproject\");",
                RuleTreeReport.exclusionSnippet(ArchitectureRule.RENAME_IS_FIELDS));
    }

    @Test
    void htmlHasAllViewsAndEveryRuleInEachOfThem() {
        String html = RuleTreeReport.toHtml(ALL, "9.9.9");
        assertTrue(html.startsWith("<!doctype html>"));
        assertFalse(html.contains("{{"), "unreplaced placeholder");
        assertTrue(html.contains("<h1>Architecture rules 9.9.9</h1>"));
        assertTrue(html.contains(ALL.size() + " rules &middot; " + RuleCategory.values().length + " categories &middot; "
                + RuleTag.values().length + " tags"));
        assertEquals(1, count(html, "<section class=\"view\" data-view=\"category\""));
        assertEquals(1, count(html, "<section class=\"view\" data-view=\"tag\""));
        assertEquals(1, count(html, "<section class=\"view\" data-view=\"since\""));
        for (ArchitectureRule rule : ALL) {
            int expected = 2 + rule.getTags().size(); // category view + since view + once per tag
            assertEquals(expected, count(html, "data-rule=\"" + rule.name() + "\""), rule.name());
        }
        assertEquals(ALL.size() + ALL.stream().mapToInt(r -> r.getTags().size()).sum() + ALL.size(), count(html, "<details class=\"rule\""));
    }

    @Test
    void htmlEscapesRuleDescriptions() {
        String html = RuleTreeReport.toHtml(ALL, "");
        assertTrue(html.contains("<h1>Architecture rules</h1>"));
        assertFalse(html.contains("<init>"), "raw ArchUnit description leaked into HTML");
        assertTrue(html.contains("Vector.&lt;init&gt;()"));
        assertTrue(html.contains("@RequestMapping"));
        assertEquals(count(html, "<details"), count(html, "</details>"), "unbalanced details");
        assertEquals(count(html, "<summary>"), count(html, "</summary>"), "unbalanced summary");
    }

    @Test
    void htmlRuleNodeShowsProgressivelyDisclosedFacts() {
        String html = RuleTreeReport.toHtml(List.of(ArchitectureRule.USE_ARRAYLIST_INSTEAD_OF_VECTOR), "");
        assertTrue(html.contains("1 rules &middot; 2 categories &middot; 3 tags"));
        assertTrue(html.contains("<details class=\"group\" data-depth=\"1\"><summary><span class=\"label\">Performance &amp; concurrency</span>"
                + "<span class=\"count\" title=\"rules\">1</span><span class=\"desc\">" + RuleCategory.PERFORMANCE.getDescription() + "</span></summary>"));
        assertTrue(html.contains("<details class=\"group\" data-depth=\"2\"><summary><span class=\"label\">Synchronized legacy classes</span>"));
        assertTrue(html.contains("<details class=\"rule\" data-rule=\"USE_ARRAYLIST_INSTEAD_OF_VECTOR\" data-tags=\"PERFORMANCE CONCURRENCY COLLECTIONS\">"
                + "<summary><code class=\"name\">USE_ARRAYLIST_INSTEAD_OF_VECTOR</code><span class=\"what\">no classes should call constructor Vector.&lt;init&gt;()</span></summary>"));
        assertTrue(html.contains("<dt>Why</dt><dd>Use ArrayList instead of Vector because Arraylist is unsynchronized and faster</dd>"));
        assertTrue(html.contains("<dt>Category</dt><dd>Performance &amp; concurrency › Synchronized legacy classes</dd>"));
        assertTrue(html.contains("<dt>Since</dt><dd>1.0.0</dd>"));
        assertTrue(html.contains("<button type=\"button\" class=\"tag\" data-tag=\"COLLECTIONS\">Collections</button>"));
        assertTrue(html.contains("href=\"#USE_ARRAYLIST_INSTEAD_OF_VECTOR\""));
        // tag and since groups have no description, so no empty desc span is rendered
        assertTrue(html.contains("<span class=\"label\">Since 1.0.0</span><span class=\"count\" title=\"rules\">1</span></summary>"));
    }

    @Test
    void jsonContainsAllViews() {
        String json = RuleTreeReport.toJson(ALL);
        assertTrue(json.startsWith("{\n  \"ruleCount\": " + ALL.size() + ",\n  \"views\": {"));
        assertTrue(json.contains("\"category\": {\"kind\":\"root\",\"id\":\"category\""));
        assertTrue(json.contains("\"tag\": {\"kind\":\"root\",\"id\":\"tag\""));
        assertTrue(json.contains("\"since\": {\"kind\":\"root\",\"id\":\"since\""));
        assertFalse(json.contains("<"), "unescaped <");
        for (ArchitectureRule rule : ALL) {
            assertEquals(2 + rule.getTags().size(), count(json, "{\"kind\":\"rule\",\"id\":\"" + rule.name() + "\""), rule.name());
        }
        assertTrue(json.contains("\"id\":\"SPRING_BOOT_SERVICES_SHOULD_NOT_CALL_CONTROLLERS\",\"label\":\"SPRING_BOOT_SERVICES_SHOULD_NOT_CALL_CONTROLLERS\""));
        assertTrue(json.contains("\"category\":\"SPRING_LAYERING_BY_PACKAGE\",\"categoryPath\":[\"SPRING\",\"SPRING_LAYERING\",\"SPRING_LAYERING_BY_PACKAGE\"],\"since\":\"2.0.0\",\"tags\":[\"SPRING\",\"LAYERING\"]}"));
        assertTrue(json.contains("{\"kind\":\"group\",\"id\":\"PERFORMANCE\",\"label\":\"Performance & concurrency\""));
        assertEquals(count(json, "{"), count(json, "}"), "unbalanced braces");
        assertEquals(count(json, "["), count(json, "]"), "unbalanced brackets");
    }

    @Test
    void jsonRuleCountsMatchTree() {
        String json = RuleTreeReport.toJson(List.of(ArchitectureRule.INTERFACES_SHOULD_NOT_END_IN_IMPL)); // single tag: NAMING
        Matcher m = Pattern.compile("\"ruleCount\":(\\d+)").matcher(json);
        int nodes = 0;
        while (m.find()) {
            assertEquals("1", m.group(1));
            nodes++;
        }
        // category: root, GENERAL_CODING, NAMING, rule = 4; tag: root, NAMING, rule = 3; since: root, 1.0.0, rule = 3
        assertEquals(10, nodes);
    }

    @Test
    void writesBothFiles(@TempDir Path dir) throws IOException {
        Path out = dir.resolve("nested/report");
        RuleTreeReport.writeTo(out, ALL, "1.2.3");
        String html = new String(Files.readAllBytes(out.resolve(RuleTreeReport.HTML_FILE)), StandardCharsets.UTF_8);
        String json = new String(Files.readAllBytes(out.resolve(RuleTreeReport.JSON_FILE)), StandardCharsets.UTF_8);
        assertEquals(RuleTreeReport.toHtml(ALL, "1.2.3"), html);
        assertEquals(RuleTreeReport.toJson(ALL), json);
    }

    @Test
    void mainWritesToTheGivenDirectory(@TempDir Path dir) throws IOException {
        RuleTreeReport.main(new String[]{dir.toString(), "7.0.0"});
        String html = new String(Files.readAllBytes(dir.resolve(RuleTreeReport.HTML_FILE)), StandardCharsets.UTF_8);
        assertTrue(html.contains("<h1>Architecture rules 7.0.0</h1>"));
        assertTrue(Files.exists(dir.resolve(RuleTreeReport.JSON_FILE)));
    }
}
