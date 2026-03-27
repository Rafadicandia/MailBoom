package com.mailboom.api.domain.model.whatsapp;

import com.mailboom.api.domain.model.whatsapp.valueobjects.AccessToken;
import lombok.Getter;

import java.util.UUID;
@Getter
public class ClientConfig {
    private final UUID clientId;
    private final String wabaId;
    private final String phoneNumberId;
    private final AccessToken accessToken;

    public ClientConfig(UUID clientId, String wabaId, String phoneNumberId, AccessToken accessToken) {
        this.clientId = clientId;
        this.wabaId = wabaId;
        this.phoneNumberId = phoneNumberId;
        this.accessToken = accessToken;
    }

}
