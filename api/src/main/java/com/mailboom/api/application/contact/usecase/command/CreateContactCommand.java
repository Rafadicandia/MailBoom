package com.mailboom.api.application.contact.usecase.command;

import java.util.Map;

public record CreateContactCommand(
        String contactListId,
        String email,
        String name,
        Map<String, Object>customFields,
        boolean subscribed

) {
    public CreateContactCommand {
        if (contactListId == null || contactListId.isBlank()) {
            throw new IllegalArgumentException("Contact list ID is required");
        }

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name is required");
        }
    }
}
