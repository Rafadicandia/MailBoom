package com.mailboom.api.application.whatsapp.usecase.command;

import com.mailboom.api.domain.model.contact.valueobjects.ContactListId;

public record SendTemplateToListCommand(
        String templateName,
        ContactListId to
) {
    public SendTemplateToListCommand {
        if (templateName == null || templateName.isBlank()) {
            throw new IllegalArgumentException("Template name is required");
        }
        if (to == null) {
            throw new IllegalArgumentException("Contact List id is required");
        }
    }
}
