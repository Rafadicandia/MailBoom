package com.mailboom.api.domain.model.valueobjects;

import com.mailboom.api.domain.exception.CampaignIdCannotBeNullException;

import java.util.UUID;

public record CampaignId(UUID value) {
    public CampaignId {
        if (value == null || value.toString().isEmpty()) {
            throw new CampaignIdCannotBeNullException("Campaign ID cannot be null or empty");
        }
    }
    public static CampaignId fromString(String id) {
        return new CampaignId(UUID.fromString(id));
    }

    public static CampaignId newId() {
        return new CampaignId(UUID.randomUUID());
    }
    @Override
    public String toString() {
        return value().toString();
    }
    public UUID value() {
        return value;
    }
}
