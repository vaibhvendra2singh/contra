package com.contra.oms.jira;

import java.util.List;
import java.util.Optional;

/** Boundary around Jira so order operations and tests do not depend on Jira availability. */
public interface JiraClient {
    Optional<IssueSummary> issue(String issueKey);
    Optional<ReleaseSummary> release(String versionName);
    List<IssueSummary> recentIssues(int limit);
    Optional<SprintSummary> activeSprint();

    record IssueSummary(String key, String summary, String status, int points, boolean openBlocker,
                        String url) { }
    record ReleaseSummary(String name, List<IssueSummary> issues) { }
    record SprintSummary(String name, String goal, int totalPoints, int completedPoints,
                         List<IssueSummary> recentIssues, String boardUrl) { }
}
