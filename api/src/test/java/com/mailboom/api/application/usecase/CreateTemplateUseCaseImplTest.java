package com.mailboom.api.application.usecase;

import com.mailboom.api.application.whatsapp.usecase.CreateTemplateUseCaseImpl;
import com.mailboom.api.application.whatsapp.usecase.command.CreateTemplateCommand;
import com.mailboom.api.domain.model.user.valueobjects.UserId;
import com.mailboom.api.domain.model.whatsapp.ClientConfig;
import com.mailboom.api.domain.model.whatsapp.Template;
import com.mailboom.api.domain.model.whatsapp.valueobjects.*;
import com.mailboom.api.domain.port.out.ClientConfigRepository;
import com.mailboom.api.domain.port.out.TemplateRepository;
import com.mailboom.api.domain.port.out.WhatsAppGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateTemplateUseCaseImplTest {

    @Mock
    private TemplateRepository templateRepository;

    @Mock
    private WhatsAppGateway whatsAppGateway;

    @Mock
    private ClientConfigRepository clientConfigRepository;

    @InjectMocks
    private CreateTemplateUseCaseImpl createTemplateUseCase;

    private UUID userId;
    private CreateTemplateCommand command;
    private ClientConfig config;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        command = new CreateTemplateCommand(
                "test_template",
                "MARKETING",
                "NAMED",
                List.of(new TemplateComponent(ComponentType.BODY, "Hello {{1}}")),
                "en_US",
                userId
        );

        config = new ClientConfig(
                new UserId(userId),
                "waba-123",
                "phone-456",
                new AccessToken("token-789")
        );
    }

    @Test
    void execute_shouldSaveTemplateAndSendToReview_whenConfigExists() {
        // Given
        when(clientConfigRepository.findById(any(UserId.class))).thenReturn(Optional.of(config));
        when(templateRepository.save(any(Template.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Template result = createTemplateUseCase.execute(command);

        // Then
        assertNotNull(result);
        assertEquals(command.name(), result.getName());
        assertEquals(Category.MARKETING, result.getCategory());
        assertEquals(TemplateStatus.PENDING, result.getStatus());
        assertEquals(new UserId(userId), result.getOwnerId());

        verify(clientConfigRepository).findById(new UserId(userId));
        verify(templateRepository).save(any(Template.class));
        verify(whatsAppGateway).sendTemplateForReview(eq(result), eq(config));
    }

    @Test
    void execute_shouldThrowException_whenConfigDoesNotExist() {
        // Given
        when(clientConfigRepository.findById(any(UserId.class))).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> createTemplateUseCase.execute(command));
        assertTrue(exception.getMessage().contains("Configuration not found"));

        verify(clientConfigRepository).findById(new UserId(userId));
        verifyNoInteractions(templateRepository);
        verifyNoInteractions(whatsAppGateway);
    }
}
