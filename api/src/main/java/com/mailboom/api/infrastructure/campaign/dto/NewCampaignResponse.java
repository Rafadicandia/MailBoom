package com.mailboom.api.infrastructure.campaign.dto;

import jakarta.validation.constraints.NotNull;

public record NewCampaignResponse(
        @NotNull String id,
        @NotNull String ownerId,
        @NotNull String subject,
        @NotNull String htmlContent,
        @NotNull String senderIdentity,
        @NotNull String recipientListId,
        @NotNull String status,
        @NotNull String createdAt
) {
}
