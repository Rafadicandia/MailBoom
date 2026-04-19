package com.mailboom.api.application.whatsapp.port.in;

import com.mailboom.api.domain.model.user.valueobjects.UserId;

import java.util.UUID;

public interface DeleteClientConfigUseCase {
    void delete(UserId clientId);
}
