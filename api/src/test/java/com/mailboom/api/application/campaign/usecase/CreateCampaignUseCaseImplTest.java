package com.mailboom.api.application.campaign.usecase;

import com.mailboom.api.application.campaign.usecase.command.CreateCampaignCommand;
import com.mailboom.api.domain.model.campaign.Campaign;
import com.mailboom.api.domain.model.common.valueobjects.Email;
import com.mailboom.api.domain.model.common.valueobjects.Name;
import com.mailboom.api.domain.model.contact.valueobjects.ContactListId;
import com.mailboom.api.domain.model.user.User;
import com.mailboom.api.domain.model.user.valueobjects.EmailCounter;
import com.mailboom.api.domain.model.user.valueobjects.PasswordHash;
import com.mailboom.api.domain.model.user.valueobjects.UserId;
import com.mailboom.api.domain.port.out.CampaignRepository;
import com.mailboom.api.domain.port.out.UserRepository;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateCampaignUseCaseImplTest {

    @Mock
    private CampaignRepository campaignRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CreateCampaignUseCaseImpl createCampaignUseCase;

    private CreateCampaignCommand command;
    private UserId userId;
    private ContactListId contactListId;
    private User user;

    @BeforeEach
    void setUp() {
        userId = new UserId(UUID.randomUUID());
        contactListId = new ContactListId(UUID.randomUUID());
        command = new CreateCampaignCommand(
                userId.value().toString(),
                "Test Subject",
                "<html><body>Content</body></html>",
                "sender",
                contactListId.value().toString()
        );
        user = User.create(
                userId,
                new Email("test@example.com"),
                new Name("Test User"),
                new PasswordHash("password"),
                EmailCounter.zero()
        );
    }

    @Test
    void shouldCreateCampaignSuccessfully() {
        // Given
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(campaignRepository.save(any(Campaign.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Campaign result = createCampaignUseCase.execute(command);

        // Then
        assertNotNull(result);
        assertEquals(userId, result.getOwner());
        assertEquals("Test Subject", result.getSubject().value());
        assertEquals("Content", result.getHtmlContent().value());
        assertEquals("sender@mailboom.email", result.getSenderIdentity().value());
        assertEquals(contactListId, result.getRecipientList());
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        // Given
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> createCampaignUseCase.execute(command));
    }
}
