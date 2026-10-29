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

    protected FeatureFlag() { }

    public FeatureFlag(String key, String description, boolean enabled) {
        this.key = key;
        this.description = description;
        this.enabled = enabled;
    }

    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public String getKey() { return key; }
    public String getDescription() { return description; }
    public boolean isEnabled() { return enabled; }
}
