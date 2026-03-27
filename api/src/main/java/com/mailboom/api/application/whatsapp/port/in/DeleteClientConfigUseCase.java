package com.mailboom.api.application.whatsapp.port.in;

import java.util.UUID;

public interface DeleteClientConfigUseCase {
    void delete(UUID clientId);
}
