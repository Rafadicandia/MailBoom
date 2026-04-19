package com.mailboom.api.domain.port.out;

import com.mailboom.api.domain.model.user.valueobjects.UserId;
import com.mailboom.api.domain.model.whatsapp.ClientConfig;
import java.util.Optional;
import java.util.UUID;

public interface ClientConfigRepository {
    ClientConfig save(ClientConfig clientConfig);
    Optional<ClientConfig> findById(UserId clientId);
    void deleteById(UserId clientId);
}
