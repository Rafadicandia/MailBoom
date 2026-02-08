package com.mailboom.api.application.campaign.usecase.command;

public record DeleteCampaignCommand(
        String campaignId

) {
    public DeleteCampaignCommand {
        if (campaignId == null) {
            throw new IllegalArgumentException("Campaign ID cannot be null");
        }

    }
}
