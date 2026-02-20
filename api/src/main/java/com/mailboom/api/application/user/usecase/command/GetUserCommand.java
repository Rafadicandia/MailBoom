package com.mailboom.api.application.user.usecase.command;

public record GetUserCommand(
        String userId
) {
    public GetUserCommand {
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("User ID cannot be null or empty");
        }
    }
}
