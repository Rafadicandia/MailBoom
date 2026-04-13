package com.mailboom.api.infrastructure.whatsapp.dto;

public record MetaTemplateResponse(
        String id,
        String status,
        String category
) {
    public MetaTemplateResponse {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Template ID cannot be null or blank");
        }
        if (status == null || status.isBlank()) {
            throw new IllegalArgumentException("Template status cannot be null or blank");
        }
        if (category == null || category.isBlank()) {
            throw new IllegalArgumentException("Template category cannot be null or blank");
        }
    }
}
