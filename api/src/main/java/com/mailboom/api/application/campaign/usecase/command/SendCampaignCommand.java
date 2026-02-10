package com.mailboom.api.application.campaign.usecase.command;

public record SendCampaignCommand(
        String campaignId,
        String ownerId
) {
    public SendCampaignCommand {
        if (campaignId == null || campaignId.isBlank()) {
            throw new IllegalArgumentException("Campaign ID cannot be null or blank");
        }
        if (ownerId == null || ownerId.isBlank()) {
            throw new IllegalArgumentException("Owner ID cannot be null or blank");
        }
    }
}
