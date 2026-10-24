package com.mailboom.api.application.whatsapp.usecase.command;

import com.mailboom.api.domain.model.whatsapp.valueobjects.TemplateComponent;

import java.util.List;
import java.util.UUID;

public record UpdateTemplateMessageCommand(
        UUID templateId,
        String name,
        String category,
        String parameterFormat,
        List<TemplateComponent> components,
        String language,
        UUID ownerId
) {
    public UpdateTemplateMessageCommand {
        if (templateId == null) {
            throw new IllegalArgumentException("Template Id is required");
        }


    }
}
