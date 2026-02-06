package com.mailboom.api.application.contact.usecase.command;

import java.util.Map;

public record UpdateContactCommand(
        String id,
        String email,
        String name,
        Map<String, Object> customFields,
        boolean subscribed

) {
    public UpdateContactCommand {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Contact ID is required");
        }

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name is required");
        }
    }


}
