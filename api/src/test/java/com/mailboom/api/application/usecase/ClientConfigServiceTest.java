package com.mailboom.api.application.usecase;

import com.mailboom.api.application.whatsapp.usecase.ClientConfigService;
import com.mailboom.api.application.whatsapp.usecase.command.SaveClientConfigCommand;
import com.mailboom.api.domain.model.user.valueobjects.UserId;
import com.mailboom.api.domain.model.whatsapp.ClientConfig;
import com.mailboom.api.domain.model.whatsapp.valueobjects.AccessToken;
import com.mailboom.api.domain.port.out.ClientConfigRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientConfigServiceTest {

    @Mock
    private ClientConfigRepository clientConfigRepository;

    @InjectMocks
    private ClientConfigService clientConfigService;

    private UserId clientId;
    private String wabaId;
    private String phoneNumberId;
    private String tokenValue;

    @BeforeEach
    void setUp() {
        clientId = UserId.generate();
        wabaId = "waba-123";
        phoneNumberId = "phone-456";
        tokenValue = "token-789";
    }

    @Test
    void save_shouldCreateNewConfig_whenClientIdIsNull() {
        // Given
        SaveClientConfigCommand command = new SaveClientConfigCommand(null, wabaId, phoneNumberId, tokenValue);
        when(clientConfigRepository.save(any(ClientConfig.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        ClientConfig result = clientConfigService.save(command);

        // Then
        assertNotNull(result.getClientId());
        assertEquals(wabaId, result.getWabaId());
        assertEquals(phoneNumberId, result.getPhoneNumberId());
        assertEquals(tokenValue, result.getAccessToken().value());
        verify(clientConfigRepository).save(any(ClientConfig.class));
    }

    @Test
    void save_shouldUpdateExistingConfig_whenClientIdIsProvided() {
        // Given
        SaveClientConfigCommand command = new SaveClientConfigCommand(clientId.toString(), wabaId, phoneNumberId, tokenValue);
        when(clientConfigRepository.save(any(ClientConfig.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        ClientConfig result = clientConfigService.save(command);

        // Then
        assertEquals(clientId, result.getClientId());
        verify(clientConfigRepository).save(any(ClientConfig.class));
    }

    @Test
    void get_shouldReturnConfig_whenExists() {
        // Given
        ClientConfig config = new ClientConfig(clientId, wabaId, phoneNumberId, new AccessToken(tokenValue));
        when(clientConfigRepository.findById(clientId)).thenReturn(Optional.of(config));

        // When
        ClientConfig result = clientConfigService.get(clientId);

        // Then
        assertEquals(clientId, result.getClientId());
        assertEquals(wabaId, result.getWabaId());
    }

    @Test
    void get_shouldThrowException_whenNotFound() {
        // Given
        when(clientConfigRepository.findById(clientId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> clientConfigService.get(clientId));
    }

    @Test
    void delete_shouldCallRepository() {
        // When
        clientConfigService.delete(clientId);

        // Then
        verify(clientConfigRepository).deleteById(clientId);
    }
}
