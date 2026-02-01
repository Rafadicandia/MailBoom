package com.mailboom.api.infrastructure.dto;

import org.antlr.v4.runtime.misc.NotNull;

public record LoginRequest(
        @NotNull String email,
        @NotNull String password
) {
}
