package com.mailboom.api.infrastructure.user.dto;

public record UpdateUserRequest(
        String name,
        String email,
        String password
) {
}
