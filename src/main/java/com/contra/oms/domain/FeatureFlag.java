package com.contra.oms.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "feature_flags")
public class FeatureFlag {
    @Id
    @Column(name = "flag_key")
    private String key;
    private String description;
    private boolean enabled;
    private String jiraIssueKey;
    private String qaIssueKey;

    protected FeatureFlag() { }

    public FeatureFlag(String key, String description, boolean enabled) {
        this(key, description, enabled, null, null);
    }

    public FeatureFlag(String key, String description, boolean enabled, String jiraIssueKey, String qaIssueKey) {
        this.key = key;
        this.description = description;
        this.enabled = enabled;
        this.jiraIssueKey = jiraIssueKey;
        this.qaIssueKey = qaIssueKey;
    }

    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public void setJiraIssueKey(String jiraIssueKey) { this.jiraIssueKey = jiraIssueKey; }
    public void setQaIssueKey(String qaIssueKey) { this.qaIssueKey = qaIssueKey; }
    public String getKey() { return key; }
    public String getDescription() { return description; }
    public boolean isEnabled() { return enabled; }
    public String getJiraIssueKey() { return jiraIssueKey; }
    public String getQaIssueKey() { return qaIssueKey; }
}
