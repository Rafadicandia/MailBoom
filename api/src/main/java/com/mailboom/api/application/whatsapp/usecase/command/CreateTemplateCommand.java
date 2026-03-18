package com.mailboom.api.application.whatsapp.usecase.command;

import java.util.UUID;

public record CreateTemplateCommand(
        String name,
        String content,
        String language,
        UUID ownerId
) {
    public CreateTemplateCommand {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name is required");
        }

        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("Content is required");
        }
        if (language == null || language.isBlank()) {
            throw new IllegalArgumentException("Language is required");
        }
        if (ownerId == null) {
            throw new IllegalArgumentException("Owner ID is required");
        }
    }
}
