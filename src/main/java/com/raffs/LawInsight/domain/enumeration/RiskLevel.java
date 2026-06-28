package com.raffs.LawInsight.domain.enumeration;

import lombok.Getter;

@Getter
public enum RiskLevel {
    LOW(1),
    MEDIUM(2),
    HIGH(3),
    CRITICAL(4);

    private final int severity;

    RiskLevel(int severity) {
        this.severity = severity;
    }
}
