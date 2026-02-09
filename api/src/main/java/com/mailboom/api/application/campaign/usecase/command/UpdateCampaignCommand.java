package com.mailboom.api.application.campaign.usecase.command;

public record UpdateCampaignCommand(
        String id,
        String subject,
        String htmlContent,
        String sender,
        String recipientListId
) {
}
