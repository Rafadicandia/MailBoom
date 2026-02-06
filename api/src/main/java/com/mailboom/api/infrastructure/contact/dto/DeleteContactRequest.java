package com.mailboom.api.infrastructure.contact.dto;

import org.antlr.v4.runtime.misc.NotNull;

public record DeleteContactRequest(
        @NotNull String contactId
) {
}
