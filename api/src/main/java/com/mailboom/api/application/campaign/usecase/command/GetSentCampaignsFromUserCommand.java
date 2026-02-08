package com.mailboom.api.application.campaign.usecase.command;

public record GetSentCampaignsFromUserCommand(
        String Userid
) {
    public GetSentCampaignsFromUserCommand {
        if (Userid == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
    }
}
