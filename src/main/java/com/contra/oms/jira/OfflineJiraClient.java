package com.contra.oms.jira;

import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/** Used when Jira is not configured; its empty result never blocks core order processing. */
@Component
@Conditional(JiraRestClient.NotConfiguredJiraCondition.class)
public class OfflineJiraClient implements JiraClient {
    @Override public Optional<IssueSummary> issue(String issueKey) { return Optional.empty(); }
    @Override public Optional<ReleaseSummary> release(String versionName) { return Optional.empty(); }
    @Override public List<IssueSummary> recentIssues(int limit) { return List.of(); }
    @Override public Optional<SprintSummary> activeSprint() { return Optional.empty(); }
}
