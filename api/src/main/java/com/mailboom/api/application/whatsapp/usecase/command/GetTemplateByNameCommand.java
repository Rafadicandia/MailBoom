package com.mailboom.api.application.whatsapp.usecase.command;

public record GetTemplateByNameCommand(
        String templateName

) {
    public GetTemplateByNameCommand {
        if (templateName == null || templateName.isBlank()) {
            throw new IllegalArgumentException("Template name is required");
        }

    }
}
