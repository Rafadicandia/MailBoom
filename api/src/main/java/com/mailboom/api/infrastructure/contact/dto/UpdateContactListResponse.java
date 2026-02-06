package com.mailboom.api.infrastructure.contact.dto;

public record UpdateContactListResponse(
        String id,
        String name,
        String ownerId,
        long totalContacts

) {
}
