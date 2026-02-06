package com.mailboom.api.infrastructure.dto;

import java.util.Map;

public record UpdateContactResponse(
        String contactId,
        String contactListId,
        String email,
        String name,
        Map<String, Object> customFields,
        boolean subscribed
) {
}
