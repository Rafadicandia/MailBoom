package com.mailboom.api.application.whatsapp.usecase.command;

import com.mailboom.api.domain.model.whatsapp.valueobjects.TemplateComponent;

import java.util.List;
import java.util.UUID;

public record CreateTemplateCommand(
        String name,
        String category,
        String parameterFormat,
        List<TemplateComponent> components,
        String language,
        UUID ownerId
) {
    public CreateTemplateCommand {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name is required");
        }

        if (category == null || category.isBlank()) {
            throw new IllegalArgumentException("Category is required");
        }
        if (parameterFormat == null || parameterFormat.isBlank()) {
            throw new IllegalArgumentException("Parameter format is required");
        }
        if (components == null || components.isEmpty()) {
            throw new IllegalArgumentException("Components are required");
        }

        if (language == null || language.isBlank()) {
            throw new IllegalArgumentException("Language is required");
        }
        if (ownerId == null) {
            throw new IllegalArgumentException("Owner ID is required");
        }
    }
}
