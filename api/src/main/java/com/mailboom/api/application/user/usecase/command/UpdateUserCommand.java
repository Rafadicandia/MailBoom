package com.mailboom.api.application.user.usecase.command;

public record UpdateUserCommand(
        String userId,
        String name,
        String email,
        String password
) {
    public UpdateUserCommand {
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("User ID cannot be null or empty");
        }
    }
}
