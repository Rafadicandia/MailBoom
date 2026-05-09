package com.mailboom.api.infrastructure.whatsapp.dto;

import java.util.UUID;

public record SendTemplateToListRequest(
        String templateName,
        UUID to

) {
}
