package com.mailboom.api.infrastructure.user.dto;

import jakarta.validation.constraints.NotNull;

public record GetAllUsersRequest(
        @NotNull String adminId
) {

}
