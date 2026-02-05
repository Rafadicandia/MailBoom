package com.mailboom.api.application.contact.usecase.command;

import java.util.UUID;

public record CreateContactListCommand(
        String name,
        UUID ownerId
) {
    public CreateContactListCommand {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name is required");
        }
        if (ownerId == null) {
            throw new IllegalArgumentException("Owner ID is required");
        }
    }
}
