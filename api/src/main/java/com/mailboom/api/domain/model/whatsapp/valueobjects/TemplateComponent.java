package com.mailboom.api.domain.model.whatsapp.valueobjects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TemplateComponent(
        ComponentType type,
        String text,
        Object example,
        List<Button> buttons
) {
    @JsonCreator
    public TemplateComponent {
    }

    public TemplateComponent(ComponentType type, String text) {
        this(type, text, null, null);
    }
}