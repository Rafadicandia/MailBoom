package com.mailboom.api.application.whatsapp.usecase.command;

import java.util.UUID;

public record SaveClientConfigCommand(
    String clientId,
    String wabaId,
    String phoneNumberId,
    String accessToken
) {}
