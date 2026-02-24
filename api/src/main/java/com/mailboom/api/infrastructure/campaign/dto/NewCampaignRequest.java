package com.mailboom.api.infrastructure.campaign.dto;


import jakarta.validation.constraints.NotNull;

public record NewCampaignRequest(
        @NotNull String ownerId,
        @NotNull String subject,
        @NotNull String htmlContent,
        @NotNull String sender,
        @NotNull String recipientListId
) {
}
