package com.mailboom.api.infrastructure.contact.dto;

import java.util.Map;

public record ContactDataResponse(
        String contactId,
        String contactListId,
        String email,
        String name,
        Map<String, Object> customFields,
        boolean subscribed
) {
}
