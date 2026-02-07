package com.mailboom.api.infrastructure.campaign.dto;

import org.antlr.v4.runtime.misc.NotNull;

public record NewCampaignRequest(
        @NotNull String ownerId,
        @NotNull String subject,
        @NotNull String htmlContent,
        @NotNull String sender,
        @NotNull String recipientListId
) {
}
