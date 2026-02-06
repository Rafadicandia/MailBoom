package com.mailboom.api.infrastructure.dto;

public record NewContactListResponse(
        String id,
        String name,
        String ownerId
) {
}
