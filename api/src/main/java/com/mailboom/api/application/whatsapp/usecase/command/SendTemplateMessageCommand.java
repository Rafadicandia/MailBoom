package com.mailboom.api.application.whatsapp.usecase.command;

import java.util.List;
import java.util.UUID;

public record SendTemplateMessageCommand(
        UUID templateId,
        String to,
        List<String> parameters
) {
    public SendTemplateMessageCommand {
        if (templateId == null) {
            throw new IllegalArgumentException("Template ID is required");
        }
        if (to == null || to.isBlank()) {
            throw new IllegalArgumentException("Recipient phone number is required");
        }
//        if (parameters == null || parameters.isEmpty()) {
//            throw new IllegalArgumentException("At least one parameter is required");
//        }
    }
}
