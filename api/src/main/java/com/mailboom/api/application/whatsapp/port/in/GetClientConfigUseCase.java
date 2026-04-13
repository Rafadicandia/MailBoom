package com.mailboom.api.application.whatsapp.port.in;

import com.mailboom.api.domain.model.user.valueobjects.UserId;
import com.mailboom.api.domain.model.whatsapp.ClientConfig;

public interface GetClientConfigUseCase {
    ClientConfig get(UserId clientId);
}
