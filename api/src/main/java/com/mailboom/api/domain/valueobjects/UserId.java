package com.mailboom.api.domain.valueobjects;

import com.mailboom.api.domain.exception.UserCannotBeNullException;

import java.util.UUID;

public record UserId(UUID userId) {
    public UserId {
        if (userId == null|| userId.toString().isEmpty()) {
            throw new UserCannotBeNullException("User ID cannot be null");
        }
    }

    public static UserId fromString(String userId) {
        return new UserId(UUID.fromString(userId));
    }

    public static UserId generate() {
        return new UserId(UUID.randomUUID());
    }

    @Override
    public String toString() {
        return userId.toString();
    }

    public static UserId random() {
        return new UserId(UUID.randomUUID());
    }


}
