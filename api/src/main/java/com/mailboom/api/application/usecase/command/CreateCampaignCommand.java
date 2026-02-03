package com.mailboom.api.application.usecase.command;

import com.mailboom.api.domain.model.valueobjects.ContactListId;

public record CreateCampaignCommand(
        String ownerId,
        String subject,
        String htmlContent,
        String sender,
        String recipientListId)
 {

}
