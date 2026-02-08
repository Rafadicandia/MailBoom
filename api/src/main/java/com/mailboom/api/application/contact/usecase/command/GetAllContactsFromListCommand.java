package com.mailboom.api.application.contact.usecase.command;

public record GetAllContactsFromListCommand(
        String listId
) {
    public GetAllContactsFromListCommand {
        if (listId == null || listId.isBlank()) {
            throw new IllegalArgumentException("List ID is required");
        }
    }
}
