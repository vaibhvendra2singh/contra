package com.contra.oms.jira;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.time.Instant;
import java.util.Map;

/** Small Jira Cloud REST adapter. Failures are contained at the service boundary. */
@Component
@Conditional(JiraRestClient.ConfiguredJiraCondition.class)
public class JiraRestClient implements JiraClient {
    public static class ConfiguredJiraCondition implements Condition {
        @Override public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            String url = context.getEnvironment().getProperty("oms.jira.base-url", "");
            return !url.isBlank() && !url.contains("your-site.atlassian.net");
        }
    }
    public static class NotConfiguredJiraCondition implements Condition {
        @Override public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            String url = context.getEnvironment().getProperty("oms.jira.base-url", "");
            return url.isBlank() || url.contains("your-site.atlassian.net");
        }
    }
    @Override public boolean available() {
        return get("/rest/api/3/project/" + UriUtils.encodePathSegment(projectKey, StandardCharsets.UTF_8)) != null;
    }
    @Override public String boardUrl() {
        return boardId.isBlank() ? "" : siteUrl + "/jira/software/projects/" + projectKey + "/boards/" + boardId;
    }
    private final RestClient rest;
    private final String baseUrl;
    private final String siteUrl;
    private final String projectKey;
    private final String boardId;

    public JiraRestClient(RestClient.Builder builder,
                          @org.springframework.beans.factory.annotation.Value("${oms.jira.base-url}") String baseUrl,
                          @org.springframework.beans.factory.annotation.Value("${oms.jira.email:}") String email,
                          @org.springframework.beans.factory.annotation.Value("${oms.jira.api-token:}") String token,
                          @org.springframework.beans.factory.annotation.Value("${oms.jira.project-key:OM2}") String projectKey,
                          @org.springframework.beans.factory.annotation.Value("${oms.jira.site-url:}") String siteUrl,
                          @org.springframework.beans.factory.annotation.Value("${oms.jira.board-id:}") String boardId) {
        this.baseUrl = baseUrl.replaceAll("/$", "");
        this.siteUrl = siteUrl == null || siteUrl.isBlank() ? this.baseUrl : siteUrl.replaceAll("/$", "");
        this.projectKey = projectKey;
        this.boardId = boardId == null ? "" : boardId.trim();
        this.rest = builder.baseUrl(this.baseUrl).defaultHeaders(h -> {
            if (!email.isBlank() && !token.isBlank()) h.setBasicAuth(email, token);
            h.setAccept(List.of(MediaType.APPLICATION_JSON));
        }).build();
    }

    @Override public Optional<IssueSummary> issue(String key) {
        JsonNode node = get("/rest/api/3/issue/" + UriUtils.encodePathSegment(key, StandardCharsets.UTF_8)
                + "?fields=summary,status,customfield_10016,issuelinks");
        return node == null || node.isMissingNode() ? Optional.empty() : Optional.of(issueFrom(node));
    }

    @Override public Optional<ReleaseSummary> release(String versionName) {
        JsonNode versions = get("/rest/api/3/project/" + projectKey + "/versions");
        if (versions == null || !versions.isArray()) return Optional.empty();
        String id = null;
        for (JsonNode version : versions) if (version.path("name").asText().equals(versionName)) id = version.path("id").asText();
        if (id == null) return Optional.empty();
        JsonNode search = search("project=" + projectKey + " AND fixVersion=" + id, 100,
                List.of("summary", "status", "customfield_10016", "issuelinks"));
        List<IssueSummary> issues = new ArrayList<>();
        if (search != null && search.path("issues").isArray()) search.path("issues").forEach(i -> issues.add(issueFrom(i)));
        return Optional.of(new ReleaseSummary(versionName, issues));
    }

    @Override public List<IssueSummary> recentIssues(int limit) {
        JsonNode search = search("project=" + projectKey + " ORDER BY updated DESC", Math.max(1, Math.min(limit, 50)),
                List.of("summary", "status", "customfield_10016", "issuelinks"));
        List<IssueSummary> issues = new ArrayList<>();
        if (search != null && search.path("issues").isArray()) search.path("issues").forEach(i -> issues.add(issueFrom(i)));
        return issues;
    }

    @Override public Optional<SprintSummary> activeSprint() {
        if (boardId.isBlank()) return Optional.empty();
        JsonNode sprints = get("/rest/agile/1.0/board/" + boardId + "/sprint?state=active");
        if (sprints == null || !sprints.path("values").isArray() || sprints.path("values").isEmpty()) return Optional.empty();
        JsonNode sprint = sprints.path("values").get(0);
        String sprintId = sprint.path("id").asText();
        // Board/sprint discovery is allowed for this scoped token, but its Agile
        // sprint-issues route may omit issue visibility. Jira search is also used
        // by readiness and returns the same account-visible sprint work reliably.
        JsonNode search = search("sprint = " + sprintId, 100,
                List.of("summary", "status", "customfield_10016", "issuelinks"));
        if (search == null || !search.path("issues").isArray() || search.path("issues").isEmpty()) {
            search = get("/rest/agile/1.0/sprint/" + sprintId
                    + "/issue?maxResults=100&fields=summary,status,customfield_10016,issuelinks");
        }
        List<IssueSummary> issues = new ArrayList<>();
        if (search != null && search.path("issues").isArray()) search.path("issues").forEach(i -> issues.add(issueFrom(i)));
        int total = issues.stream().mapToInt(IssueSummary::points).sum();
        int done = issues.stream().filter(i -> "Done".equalsIgnoreCase(i.status())).mapToInt(IssueSummary::points).sum();
        return Optional.of(new SprintSummary(sprint.path("name").asText(), sprint.path("goal").asText(), total, done,
                issues.stream().limit(10).toList(), siteUrl + "/jira/software/projects/" + projectKey + "/boards/" + boardId));
    }

    @Override public Optional<String> createOperationalBug(String orderId, String status, Instant occurredAt) {
        String summary = "Order " + orderId + " stuck in " + status;
        String jql = "project=" + projectKey + " AND issuetype=Bug AND labels=auto-created AND summary ~ \"" + orderId + "\" AND statusCategory != Done";
        JsonNode found = search(jql, 1, List.of("key"));
        if (found != null && found.path("issues").isArray() && !found.path("issues").isEmpty())
            return Optional.of(found.path("issues").get(0).path("key").asText());
        try {
            Map<String, Object> description = Map.of("type", "doc", "version", 1, "content", List.of(
                    Map.of("type", "paragraph", "content", List.of(Map.of("type", "text", "text",
                            "Order ID: " + orderId + "; status: " + status + "; observed at: " + occurredAt)))));
            Map<String, Object> fields = Map.of("project", Map.of("key", projectKey),
                    "issuetype", Map.of("name", "Bug"), "summary", summary,
                    "labels", List.of("auto-created"), "description", description);
            JsonNode created = rest.post().uri("/rest/api/3/issue").contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("fields", fields))
                    .retrieve().body(JsonNode.class);
            return created == null ? Optional.empty() : Optional.ofNullable(created.path("key").asText(null));
        } catch (RuntimeException unavailable) { return Optional.empty(); }
    }

    private JsonNode get(String path) {
        try { return rest.get().uri(path).retrieve().body(JsonNode.class); }
        catch (RuntimeException unavailable) { return null; }
    }

    private JsonNode search(String jql, int maxResults, List<String> fields) {
        try {
            return rest.post().uri("/rest/api/3/search/jql").contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("jql", jql, "maxResults", maxResults, "fields", fields))
                    .retrieve().body(JsonNode.class);
        } catch (RuntimeException unavailable) { return null; }
    }

    private IssueSummary issueFrom(JsonNode issue) {
        JsonNode fields = issue.path("fields");
        JsonNode links = fields.path("issuelinks");
        boolean blocker = false;
        if (links.isArray()) for (JsonNode link : links) {
            String type = link.path("type").path("name").asText("");
            if (type.toLowerCase().contains("block") && (link.has("outwardIssue") || link.has("inwardIssue"))) {
                JsonNode other = link.has("outwardIssue") ? link.path("outwardIssue") : link.path("inwardIssue");
                if (!"Done".equalsIgnoreCase(other.path("fields").path("status").path("name").asText())) blocker = true;
            }
        }
        return new IssueSummary(issue.path("key").asText(), fields.path("summary").asText(),
                fields.path("status").path("name").asText(), fields.path("customfield_10016").asInt(0), blocker,
                siteUrl + "/browse/" + issue.path("key").asText());
    }
}
