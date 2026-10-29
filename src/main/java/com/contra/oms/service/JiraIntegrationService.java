package com.contra.oms.service;

import com.contra.oms.api.ApiModels;
import com.contra.oms.domain.FeatureFlag;
import com.contra.oms.jira.JiraClient;
import com.contra.oms.repository.FeatureFlagRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.time.Instant;

@Service
public class JiraIntegrationService {
    private final JiraClient jira;
    private final FeatureFlagRepository flags;

    public JiraIntegrationService(JiraClient jira, FeatureFlagRepository flags) {
        this.jira = jira;
        this.flags = flags;
    }

    public ApiModels.ReleaseReadinessView readiness(String version) {
        Optional<JiraClient.ReleaseSummary> response = jira.release(version);
        if (response.isEmpty()) return new ApiModels.ReleaseReadinessView(version, false, false,
                List.of("Jira is unavailable or the version does not exist."), Map.of(), 0, 0, List.of());
        List<JiraClient.IssueSummary> issues = response.get().issues();
        Map<String, List<ApiModels.JiraIssueView>> grouped = new LinkedHashMap<>();
        for (JiraClient.IssueSummary issue : issues) grouped.computeIfAbsent(issue.status(), k -> new ArrayList<>()).add(view(issue));
        int total = issues.stream().mapToInt(JiraClient.IssueSummary::points).sum();
        int done = issues.stream().filter(i -> "Done".equalsIgnoreCase(i.status())).mapToInt(JiraClient.IssueSummary::points).sum();
        List<JiraClient.IssueSummary> blockers = issues.stream().filter(JiraClient.IssueSummary::openBlocker).toList();
        List<String> reasons = new ArrayList<>();
        if (issues.isEmpty()) reasons.add("The Jira version has no assigned issues.");
        if (issues.stream().anyMatch(i -> !"Done".equalsIgnoreCase(i.status()))) reasons.add("Some version issues are not Done.");
        if (!blockers.isEmpty()) reasons.add("Open blockers or unresolved blocking dependencies remain.");
        return new ApiModels.ReleaseReadinessView(version, true, reasons.isEmpty(), reasons, grouped, done, total,
                blockers.stream().map(this::view).toList());
    }

    public ApiModels.JiraPanelView dashboardPanel() {
        Optional<JiraClient.SprintSummary> active = jira.activeSprint();
        return active.map(s -> new ApiModels.JiraPanelView(true, s.boardUrl(), s.name(), s.goal(), s.completedPoints(),
                        s.totalPoints(), s.recentIssues().stream().map(this::view).toList()))
                .orElseGet(() -> new ApiModels.JiraPanelView(false, "", "", "", 0, 0, List.of()));
    }

    public Optional<String> createOperationalBug(String orderId, String status, Instant occurredAt) {
        try { return jira.createOperationalBug(orderId, status, occurredAt); }
        catch (RuntimeException unavailable) { return Optional.empty(); }
    }

    @Transactional
    public ApiModels.FeatureFlagView toggleFlag(String key, boolean enabled) {
        FeatureFlag flag = flags.findById(key).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Feature flag not found"));
        if (enabled && (!done(flag.getJiraIssueKey()) || !done(flag.getQaIssueKey()))) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Cannot enable flag: linked Jira story and QA task must both be Done; Jira must be available.");
        }
        flag.setEnabled(enabled);
        return flagView(flag);
    }

    private boolean done(String key) {
        return key != null && !key.isBlank() && jira.issue(key).map(i -> "Done".equalsIgnoreCase(i.status())).orElse(false);
    }

    private ApiModels.FeatureFlagView flagView(FeatureFlag f) {
        return new ApiModels.FeatureFlagView(f.getKey(), f.getDescription(), f.isEnabled(), f.getJiraIssueKey(), f.getQaIssueKey());
    }

    private ApiModels.JiraIssueView view(JiraClient.IssueSummary issue) {
        return new ApiModels.JiraIssueView(issue.key(), issue.summary(), issue.status(), issue.points(), issue.openBlocker(), issue.url());
    }
}
