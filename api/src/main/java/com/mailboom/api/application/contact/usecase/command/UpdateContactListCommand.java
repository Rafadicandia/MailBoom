package com.mailboom.api.application.contact.usecase.command;

public record UpdateContactListCommand(
        String id,
        String name,
        String ownerId
) {
    public UpdateContactListCommand {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("ID is required");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name is required");
        }
        if (ownerId == null || ownerId.isBlank()) {
            throw new IllegalArgumentException("Owner ID is required");
        }
    }
}
