package com.mailboom.api.application.contact.usecase.command;

public record DeleteContactListCommand(
        String id

) {
    public DeleteContactListCommand {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Contact List ID is required");
        }
    }
}
