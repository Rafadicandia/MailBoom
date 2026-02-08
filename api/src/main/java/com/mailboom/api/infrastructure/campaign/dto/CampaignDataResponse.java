package com.mailboom.api.infrastructure.campaign.dto;

public record CampaignDataResponse(
        String id,
        String ownerId,
        String subject,
        String htmlContent,
        String senderIdentity,
        String recipientListId,
        String status,
        String createdAt
) {
}
