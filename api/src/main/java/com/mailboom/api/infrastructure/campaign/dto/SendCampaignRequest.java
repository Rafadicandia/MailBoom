package com.mailboom.api.infrastructure.campaign.dto;

public record SendCampaignRequest(
        String campaignId,
        String ownerId
) {
    public SendCampaignRequest {
        if (campaignId == null || campaignId.isBlank()) {
            throw new IllegalArgumentException("Campaign ID cannot be null or blank");
        }
        if (ownerId == null || ownerId.isBlank()) {
            throw new IllegalArgumentException("Owner ID cannot be null or blank");
        }
    }
}
