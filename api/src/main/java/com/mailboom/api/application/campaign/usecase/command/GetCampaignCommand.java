package com.mailboom.api.application.campaign.usecase.command;

public record GetCampaignCommand(
        String id
) {
    public GetCampaignCommand {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Campaign ID is required");
        }
    }
}
