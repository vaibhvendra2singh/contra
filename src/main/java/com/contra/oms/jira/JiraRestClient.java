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
    private final RestClient rest;
    private final String baseUrl;
    private final String projectKey;

    public JiraRestClient(RestClient.Builder builder,
                          @org.springframework.beans.factory.annotation.Value("${oms.jira.base-url}") String baseUrl,
                          @org.springframework.beans.factory.annotation.Value("${oms.jira.email:}") String email,
                          @org.springframework.beans.factory.annotation.Value("${oms.jira.api-token:}") String token,
                          @org.springframework.beans.factory.annotation.Value("${oms.jira.project-key:OM2}") String projectKey) {
        this.baseUrl = baseUrl.replaceAll("/$", "");
        this.projectKey = projectKey;
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
        JsonNode search = get("/rest/api/3/search?jql=" + UriUtils.encodeQueryParam(
                "project=" + projectKey + " AND fixVersion=" + id, StandardCharsets.UTF_8)
                + "&maxResults=100&fields=summary,status,customfield_10016,issuelinks");
        List<IssueSummary> issues = new ArrayList<>();
        if (search != null && search.path("issues").isArray()) search.path("issues").forEach(i -> issues.add(issueFrom(i)));
        return Optional.of(new ReleaseSummary(versionName, issues));
    }

    @Override public List<IssueSummary> recentIssues(int limit) {
        JsonNode search = get("/rest/api/3/search?jql=" + UriUtils.encodeQueryParam("project=" + projectKey + " ORDER BY updated DESC", StandardCharsets.UTF_8)
                + "&maxResults=" + Math.max(1, Math.min(limit, 50)) + "&fields=summary,status,customfield_10016,issuelinks");
        List<IssueSummary> issues = new ArrayList<>();
        if (search != null && search.path("issues").isArray()) search.path("issues").forEach(i -> issues.add(issueFrom(i)));
        return issues;
    }

    @Override public Optional<SprintSummary> activeSprint() {
        JsonNode boards = get("/rest/agile/1.0/board?projectKeyOrId=" + projectKey + "&type=scrum");
        if (boards == null || !boards.path("values").isArray() || boards.path("values").isEmpty()) return Optional.empty();
        String boardId = boards.path("values").get(0).path("id").asText();
        JsonNode sprints = get("/rest/agile/1.0/board/" + boardId + "/sprint?state=active");
        if (sprints == null || !sprints.path("values").isArray() || sprints.path("values").isEmpty()) return Optional.empty();
        JsonNode sprint = sprints.path("values").get(0);
        JsonNode search = get("/rest/agile/1.0/sprint/" + sprint.path("id").asText() + "/issue?maxResults=100&fields=summary,status,customfield_10016,issuelinks");
        List<IssueSummary> issues = new ArrayList<>();
        if (search != null && search.path("issues").isArray()) search.path("issues").forEach(i -> issues.add(issueFrom(i)));
        int total = issues.stream().mapToInt(IssueSummary::points).sum();
        int done = issues.stream().filter(i -> "Done".equalsIgnoreCase(i.status())).mapToInt(IssueSummary::points).sum();
        return Optional.of(new SprintSummary(sprint.path("name").asText(), sprint.path("goal").asText(), total, done,
                issues.stream().limit(10).toList(), baseUrl + "/jira/software/projects/" + projectKey + "/boards/" + boardId));
    }

    private JsonNode get(String path) {
        try { return rest.get().uri(path).retrieve().body(JsonNode.class); }
        catch (RuntimeException unavailable) { return null; }
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
                baseUrl + "/browse/" + issue.path("key").asText());
    }
}
