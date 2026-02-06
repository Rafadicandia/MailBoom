package com.mailboom.api.infrastructure.contact.dto;

public record NewContactListResponse(
        String id,
        String name,
        String ownerId
) {
}
