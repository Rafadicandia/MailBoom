package com.mailboom.api.domain.model.campaign.valueobjects;

public enum MailBoomDomain {
    DOMAIN("@mailboom.email")
    ;

    public final String domain;

    MailBoomDomain(String domain) {
        this.domain = domain;
    }
}
