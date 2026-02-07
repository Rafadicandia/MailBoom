package com.mailboom.api.application.campaign.usecase.command;

public record CreateCampaignCommand(
        String ownerId,
        String subject,
        String htmlContent,
        String sender,
        String recipientListId

) {
    public CreateCampaignCommand {
        if (ownerId == null) {
            throw new IllegalArgumentException("Owner ID cannot be null");
        }
        if (subject == null) {
            throw new IllegalArgumentException("Subject cannot be null");
        }
        if (htmlContent == null) {
            throw new IllegalArgumentException("HTML content cannot be null");
        }
        if (sender == null) {
            throw new IllegalArgumentException("Sender cannot be null");
        }
        if (recipientListId == null) {
            throw new IllegalArgumentException("Recipient list ID cannot be null");
        }
    }
}
