package com.mailboom.api.infrastructure.whatsapp.dto;

import com.mailboom.api.domain.model.whatsapp.valueobjects.TemplateComponent;

import java.util.List;

public record CreateTemplateResponse(
        String id,
        String name,
        String category,
        String language,
        List<TemplateComponent> components,
        String status,
        String ownerId
) {
}
