package com.mailboom.api.infrastructure.campaign.dto;

public record UpdateCampaignRequest(
        String ownerId,
        String subject,
        String htmlContent,
        String sender,
        String recipientListId
) {
    public UpdateCampaignRequest {
        if (subject == null || subject.isBlank()) {
            throw new IllegalArgumentException("Subject is required");
        }
        if (htmlContent == null || htmlContent.isBlank()) {
            throw new IllegalArgumentException("HTML content is required");
        }
        if (sender == null || sender.isBlank()) {
            throw new IllegalArgumentException("Sender is required");
        }
        if (recipientListId == null || recipientListId.isBlank()) {
            throw new IllegalArgumentException("Recipient list ID is required");
        }
    }
}
