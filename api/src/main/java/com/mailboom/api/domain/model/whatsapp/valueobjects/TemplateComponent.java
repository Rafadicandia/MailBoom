package com.mailboom.api.domain.model.whatsapp.valueobjects;

public record TemplateComponent(
        ComponentType type,
        String text
) {
    // Validaciones de dominio (ej: máximo 1024 caracteres para BODY)
    public TemplateComponent {
        if (type == ComponentType.BODY && text.length() > 1024) {
            throw new IllegalArgumentException("El cuerpo no puede superar los 1024 caracteres");
        }
    }
}

