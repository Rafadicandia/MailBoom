package com.mailboom.api.infrastructure.whatsapp.persistence.adapter;

import com.mailboom.api.domain.model.user.valueobjects.UserId;
import com.mailboom.api.domain.model.whatsapp.ClientConfig;
import com.mailboom.api.domain.model.whatsapp.valueobjects.AccessToken;
import com.mailboom.api.domain.port.out.ClientConfigRepository;
import com.mailboom.api.infrastructure.whatsapp.persistence.jpa.repository.ClientConfigJpaRepository;
import com.mailboom.api.infrastructure.whatsapp.persistence.jpa.entity.ClientConfigEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ClientConfigRepositoryAdapter implements ClientConfigRepository {

    private final ClientConfigJpaRepository jpaRepository;

    @Override
    public ClientConfig save(ClientConfig clientConfig) {
        ClientConfigEntity entity = new ClientConfigEntity(
            clientConfig.getClientId().value(),
            clientConfig.getWabaId(),
            clientConfig.getPhoneNumberId(),
            clientConfig.getAccessToken().value()
        );
        jpaRepository.save(entity);
        return clientConfig;
    }

    @Override
    public Optional<ClientConfig> findById(UserId clientId) {
        return jpaRepository.findById(clientId.value())
            .map(entity -> new ClientConfig(
                new UserId(entity.getClientId()),
                entity.getWabaId(),
                entity.getPhoneNumberId(),
                new AccessToken(entity.getAccessToken())
            ));
    }

    @Override
    public void deleteById(UserId clientId) {
        jpaRepository.deleteById(clientId.value());
    }
}
