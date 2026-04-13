package com.mailboom.api.domain.model.whatsapp;

import com.mailboom.api.domain.model.user.valueobjects.UserId;
import com.mailboom.api.domain.model.whatsapp.valueobjects.AccessToken;
import lombok.Getter;

import java.util.UUID;
@Getter
public class ClientConfig {
    private final UserId clientId;
    private final String wabaId;
    private final String phoneNumberId;
    private final AccessToken accessToken;

    public ClientConfig(UserId clientId, String wabaId, String phoneNumberId, AccessToken accessToken) {
        this.clientId = clientId;
        this.wabaId = wabaId;
        this.phoneNumberId = phoneNumberId;
        this.accessToken = accessToken;
    }

}
