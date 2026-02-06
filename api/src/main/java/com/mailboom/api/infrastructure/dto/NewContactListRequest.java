package com.mailboom.api.infrastructure.dto;

import org.antlr.v4.runtime.misc.NotNull;


public record NewContactListRequest(
        @NotNull String name,
        @NotNull String ownerId
) {
}
