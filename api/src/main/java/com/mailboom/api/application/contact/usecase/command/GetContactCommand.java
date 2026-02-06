package com.mailboom.api.application.contact.usecase.command;

public record GetContactCommand(
        String id
) {
    public GetContactCommand {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Contact ID is required");
        }
    }
}
