package com.mailboom.api.infrastructure.contact.dto;

import java.util.Map;

public record ContactData(
        String email,
        String name,
        Map<String, Object> attributes
) {
}
