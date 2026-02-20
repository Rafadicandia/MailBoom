package com.mailboom.api.infrastructure.user.dto;

import com.mailboom.api.domain.model.user.User;

public record UserResponse(
        String id,
        String email,
        String name,
        String plan,
        int emailsSent,
        String role
) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId().toString(),
                user.getEmail().toString(),
                user.getName().toString(),
                user.getPlan().name(),
                user.getEmailCounter().amountOfEmails(),
                user.getRole().name()
        );
    }
}
