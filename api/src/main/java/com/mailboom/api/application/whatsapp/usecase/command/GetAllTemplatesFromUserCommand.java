package com.mailboom.api.application.whatsapp.usecase.command;

public record GetAllTemplatesFromUserCommand(
        String userId
) {
    public GetAllTemplatesFromUserCommand {
        if (userId == null || userId.isBlank()) {

            throw new IllegalArgumentException("User Id is required");

        }
    }
}
