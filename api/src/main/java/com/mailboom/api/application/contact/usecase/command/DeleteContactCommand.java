package com.mailboom.api.application.contact.usecase.command;

public record DeleteContactCommand(
        String id
) {
    public DeleteContactCommand {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Contact ID is required");
        }
    }
}
