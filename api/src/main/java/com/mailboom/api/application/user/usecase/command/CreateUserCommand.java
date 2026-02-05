package com.mailboom.api.application.user.usecase.command;

public record CreateUserCommand(
    String email,
    String password,
    String name
) {
    public CreateUserCommand {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (password == null || password.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters");
        }
        if (name == null) {
            throw new IllegalArgumentException("Name is required");
        }
    }
}
