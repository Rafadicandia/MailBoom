package com.mailboom.api.application.contact.usecase.command;

public record GetContactListFromUserCommand(
        String ownerId
) {
    public GetContactListFromUserCommand {
        if (ownerId == null || ownerId.isBlank()) {
            throw new IllegalArgumentException("Owner ID is required");
        }
    }
}
