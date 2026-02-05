package com.mailboom.api.domain.model.user.valueobjects;

public enum PlanType {
    FREE(100, false),
    BASIC(1000, false),
    PRO(5000, false),
    ENTERPRISE(50000, true);

    private final int emailLimit;
    private final boolean isUnlimited;

    PlanType(int emailLimit, boolean isUnlimited) {
        this.emailLimit = emailLimit;
        this.isUnlimited = isUnlimited;
    }

    public int getEmailLimit() {
        return emailLimit;
    }

    public boolean isExpired() {
        // In a real implementation, this would check against a subscription expiration date
        // For now, we'll assume plans don't expire unless they're marked as expired
        return false;
    }

    public boolean isUnlimited() {
        return isUnlimited;
    }
}
