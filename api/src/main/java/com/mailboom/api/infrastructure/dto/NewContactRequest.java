package com.mailboom.api.infrastructure.dto;

import org.antlr.v4.runtime.misc.NotNull;

import java.util.Map;


public record NewContactRequest(
        @NotNull String contactListId,
        @NotNull String email,
        @NotNull String name,
        @NotNull Map<String, Object> customFields,
        boolean subscribed

        ) {
}
