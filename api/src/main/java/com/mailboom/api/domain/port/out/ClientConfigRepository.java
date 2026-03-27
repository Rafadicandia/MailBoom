package com.mailboom.api.domain.port.out;

import com.mailboom.api.domain.model.whatsapp.ClientConfig;
import java.util.Optional;
import java.util.UUID;

public interface ClientConfigRepository {
    ClientConfig save(ClientConfig clientConfig);
    Optional<ClientConfig> findById(UUID clientId);
    void deleteById(UUID clientId);
}
