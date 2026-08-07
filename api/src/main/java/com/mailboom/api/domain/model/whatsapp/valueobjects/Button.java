package com.mailboom.api.domain.model.whatsapp.valueobjects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Button(
        String type,
        String text,
        String url
) {
}
