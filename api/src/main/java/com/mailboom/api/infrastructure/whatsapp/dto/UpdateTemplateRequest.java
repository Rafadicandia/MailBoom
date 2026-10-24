package com.mailboom.api.infrastructure.whatsapp.dto;

import com.mailboom.api.domain.model.whatsapp.valueobjects.TemplateComponent;

import java.util.List;
import java.util.UUID;

public record UpdateTemplateRequest(
        UUID templateId,
        String name,
        String category,
        String parameterFormat,
        List<TemplateComponent> components,
        String language,
        UUID ownerId
) {
    public UpdateTemplateRequest {
        if (templateId == null) {
            throw new IllegalArgumentException("Template id is required");
        }

    }
}

