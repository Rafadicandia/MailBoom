package com.mailboom.api.application.contact.usecase.command;

public record GetContactListCommand(
        String id
) {
    public GetContactListCommand {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("ID is required");
        }
    }
}
