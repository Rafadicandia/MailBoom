package com.mailboom.api.infrastructure.whatsapp.dto;
import com.mailboom.api.domain.model.whatsapp.valueobjects.TemplateComponent;

public record MetaTemplateRequest(
        String name,
        String category,
        String language,
        TemplateComponent[] components
) {
    public MetaTemplateRequest {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name is required");
        }
        if (category == null || category.isBlank()) {
            throw new IllegalArgumentException("Category is required");
        }
        if (language == null || language.isBlank()) {
            throw new IllegalArgumentException("Language is required");
        }

        if (components == null || components.length == 0) {
            throw new IllegalArgumentException("At least one component is required");
        }
    }
}

