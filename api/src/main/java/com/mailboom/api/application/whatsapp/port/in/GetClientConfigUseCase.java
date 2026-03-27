package com.mailboom.api.application.whatsapp.port.in;

import com.mailboom.api.domain.model.whatsapp.ClientConfig;
import java.util.UUID;

public interface GetClientConfigUseCase {
    ClientConfig get(UUID clientId);
}
