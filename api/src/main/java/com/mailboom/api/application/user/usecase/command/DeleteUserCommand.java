package com.mailboom.api.application.user.usecase.command;

public record DeleteUserCommand(
        String userId
) {
    public DeleteUserCommand {
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("User ID cannot be null or empty");
        }
    }
}
