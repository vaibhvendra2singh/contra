package com.contra.oms.service;

import com.contra.oms.domain.FeatureFlag;
import com.contra.oms.jira.JiraClient;
import com.contra.oms.repository.FeatureFlagRepository;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JiraIntegrationServiceTest {
    @Test
    void offlineJiraProducesNotReadyAndDoesNotBreakReadiness() {
        JiraClient jira = mock(JiraClient.class);
        when(jira.release("v1.0")).thenReturn(Optional.empty());
        JiraIntegrationService service = new JiraIntegrationService(jira, mock(FeatureFlagRepository.class));

        var readiness = service.readiness("v1.0");

        assertFalse(readiness.jiraAvailable());
        assertFalse(readiness.ready());
        assertFalse(readiness.reasons().isEmpty());
    }

    @Test
    void flagCannotBeEnabledWithoutDoneStoryAndQaTask() {
        JiraClient jira = mock(JiraClient.class);
        FeatureFlagRepository flags = mock(FeatureFlagRepository.class);
        FeatureFlag flag = new FeatureFlag("new-checkout", "New checkout", false, "OM2-9", null);
        when(flags.findById("new-checkout")).thenReturn(Optional.of(flag));
        when(jira.issue("OM2-9")).thenReturn(Optional.of(new JiraClient.IssueSummary("OM2-9", "Checkout", "Done", 5, false, "url")));
        JiraIntegrationService service = new JiraIntegrationService(jira, flags);

        assertThrows(ResponseStatusException.class, () -> service.toggleFlag("new-checkout", true));
        assertFalse(flag.isEnabled());
    }

    @Test
    void flagEnablesOnlyAfterBothLinkedIssuesAreDone() {
        JiraClient jira = mock(JiraClient.class);
        FeatureFlagRepository flags = mock(FeatureFlagRepository.class);
        FeatureFlag flag = new FeatureFlag("new-checkout", "New checkout", false, "OM2-9", "OM2-34");
        when(flags.findById("new-checkout")).thenReturn(Optional.of(flag));
        when(jira.issue("OM2-9")).thenReturn(Optional.of(new JiraClient.IssueSummary("OM2-9", "Checkout", "Done", 5, false, "url")));
        when(jira.issue("OM2-34")).thenReturn(Optional.of(new JiraClient.IssueSummary("OM2-34", "QA checkout", "Done", 0, false, "url")));
        JiraIntegrationService service = new JiraIntegrationService(jira, flags);

        var result = service.toggleFlag("new-checkout", true);

        assertTrue(result.enabled());
        assertEquals("OM2-34", result.qaIssueKey());
    }

    @Test
    void unavailableJiraFailsClosedForEnableButAllowsDisable() {
        JiraClient jira = mock(JiraClient.class);
        FeatureFlagRepository flags = mock(FeatureFlagRepository.class);
        FeatureFlag flag = new FeatureFlag("new-checkout", "New checkout", false, "OM2-9", "OM2-34");
        when(flags.findById("new-checkout")).thenReturn(Optional.of(flag));
        when(jira.issue("OM2-9")).thenReturn(Optional.empty());
        when(jira.issue("OM2-34")).thenReturn(Optional.empty());
        JiraIntegrationService service = new JiraIntegrationService(jira, flags);

        assertThrows(ResponseStatusException.class, () -> service.toggleFlag("new-checkout", true));
        assertFalse(service.toggleFlag("new-checkout", false).enabled());
    }

    @Test
    void readinessRequiresAllAssignedIssuesDoneAndNoOpenBlockers() {
        JiraClient jira = mock(JiraClient.class);
        when(jira.release("v1.0")).thenReturn(Optional.of(new JiraClient.ReleaseSummary("v1.0", List.of(
                new JiraClient.IssueSummary("OM2-1", "Done story", "Done", 5, false, "url"),
                new JiraClient.IssueSummary("OM2-2", "Blocked story", "In Progress", 3, true, "url")))));
        JiraIntegrationService service = new JiraIntegrationService(jira, mock(FeatureFlagRepository.class));

        var readiness = service.readiness("v1.0");

        assertTrue(readiness.jiraAvailable());
        assertFalse(readiness.ready());
        assertEquals(8, readiness.totalPoints());
        assertEquals(5, readiness.donePoints());
        assertEquals(1, readiness.blockers().size());
    }

    @Test
    void operationalBugReportingIsBestEffortWhenJiraFails() {
        JiraClient jira = mock(JiraClient.class);
        when(jira.createOperationalBug("order-1", "PAYMENT_FAILED", Instant.EPOCH))
                .thenThrow(new IllegalStateException("Jira offline"));
        JiraIntegrationService service = new JiraIntegrationService(jira, mock(FeatureFlagRepository.class));

        assertTrue(service.createOperationalBug("order-1", "PAYMENT_FAILED", Instant.EPOCH).isEmpty());
    }
}
