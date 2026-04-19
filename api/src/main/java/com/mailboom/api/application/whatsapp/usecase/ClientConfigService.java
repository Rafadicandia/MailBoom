package com.mailboom.api.application.whatsapp.usecase;

import com.mailboom.api.application.whatsapp.port.in.DeleteClientConfigUseCase;
import com.mailboom.api.application.whatsapp.port.in.GetClientConfigUseCase;
import com.mailboom.api.application.whatsapp.port.in.SaveClientConfigUseCase;
import com.mailboom.api.application.whatsapp.usecase.command.SaveClientConfigCommand;
import com.mailboom.api.domain.model.user.valueobjects.UserId;
import com.mailboom.api.domain.model.whatsapp.ClientConfig;
import com.mailboom.api.domain.model.whatsapp.valueobjects.AccessToken;
import com.mailboom.api.domain.port.out.ClientConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClientConfigService implements SaveClientConfigUseCase, GetClientConfigUseCase, DeleteClientConfigUseCase {

    private final ClientConfigRepository clientConfigRepository;

    @Override
    @Transactional
    public ClientConfig save(SaveClientConfigCommand command) {
        ClientConfig config = new ClientConfig(
                command.clientId() == null ? UserId.generate() : new UserId(UUID.fromString(command.clientId())),
                command.wabaId(),
                command.phoneNumberId(),
                new AccessToken(command.accessToken())
        );
        return clientConfigRepository.save(config);
    }

    @Override
    @Transactional(readOnly = true)
    public ClientConfig get(UserId clientId) {
        return clientConfigRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client configuration not found for id: " + clientId));
    }

    @Override
    @Transactional
    public void delete(UserId clientId) {
        clientConfigRepository.deleteById(clientId);
    }
}
