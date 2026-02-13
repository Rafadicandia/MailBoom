package com.mailboom.api.application.campaign.usecase;

import com.mailboom.api.application.campaign.usecase.command.SendCampaignCommand;
import com.mailboom.api.application.common.exception.ContactListSizeExceedsLimitException;
import com.mailboom.api.domain.model.campaign.Campaign;
import com.mailboom.api.domain.model.campaign.valueobjects.*;
import com.mailboom.api.domain.model.contact.Contact;
import com.mailboom.api.domain.model.contact.valueobjects.ContactListId;
import com.mailboom.api.domain.model.user.User;
import com.mailboom.api.domain.model.user.valueobjects.PlanType;
import com.mailboom.api.domain.model.user.valueobjects.UserId;
import com.mailboom.api.domain.port.out.CampaignRepository;
import com.mailboom.api.domain.port.out.ContactRepository;
import com.mailboom.api.domain.port.out.EmailSender;
import com.mailboom.api.domain.port.out.UserRepository;
import com.mailboom.api.infrastructure.common.exception.EmailSendingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SendCampaignUseCaseImplTest {

    @Mock
    private EmailSender emailSender;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CampaignRepository campaignRepository;
    @Mock
    private ContactRepository contactRepository;

    @InjectMocks
    private SendCampaignUseCaseImpl sendCampaignUseCase;

    private SendCampaignCommand command;
    private UserId userId;
    private CampaignId campaignId;
    private ContactListId contactListId;
    private Campaign campaign;
    private User user;

    @BeforeEach
    void setUp() {
        userId = new UserId(UUID.randomUUID());
        campaignId = new CampaignId(UUID.randomUUID());
        contactListId = new ContactListId(UUID.randomUUID());
        command = new SendCampaignCommand(campaignId.value().toString(), userId.value().toString());

        campaign = mock(Campaign.class);
        user = mock(User.class);
    }

    private void setupReadyToSend() {
        when(campaign.getStatus()).thenReturn(CampaignStatus.DRAFT);
        when(campaign.getRecipientList()).thenReturn(contactListId);
        when(campaign.getSenderIdentity()).thenReturn(new EmailSenderIdentity("test"));
        when(campaign.getSubject()).thenReturn(new Subject("test"));
        when(campaign.getHtmlContent()).thenReturn(new HtmlContent("test"));
    }

    @Test
    void shouldSendCampaignSuccessfully() {
        // Given
        setupReadyToSend();
        when(campaign.getOwner()).thenReturn(userId);
        when(campaign.getRecipientList()).thenReturn(contactListId);
        when(campaignRepository.findById(campaignId)).thenReturn(Optional.of(campaign));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(contactRepository.findAllByContactListId(contactListId)).thenReturn(List.of(mock(Contact.class)));
        when(user.canSendMoreEmails(1)).thenReturn(true);
        when(campaign.markAsSending()).thenReturn(campaign);
        when(campaign.markAsSent()).thenReturn(campaign);
        when(user.incrementEmailsSent(1)).thenReturn(user);

        // When
        sendCampaignUseCase.execute(command);

        // Then
        verify(emailSender).send(campaign);
        verify(campaignRepository, times(2)).save(campaign);
        verify(userRepository).save(user);
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        // Given
        when(campaignRepository.findById(campaignId)).thenReturn(Optional.of(campaign));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> sendCampaignUseCase.execute(command));
    }

    @Test
    void shouldThrowExceptionWhenContactListIsEmpty() {
        // Given
        when(campaign.getRecipientList()).thenReturn(contactListId);
        when(campaignRepository.findById(campaignId)).thenReturn(Optional.of(campaign));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(contactRepository.findAllByContactListId(contactListId)).thenReturn(Collections.emptyList());

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> sendCampaignUseCase.execute(command));
    }

    @Test
    void shouldThrowExceptionWhenUserIsNotOwner() {
        // Given
        when(campaign.getOwner()).thenReturn(new UserId(UUID.randomUUID()));
        when(campaign.getRecipientList()).thenReturn(contactListId);
        when(campaignRepository.findById(campaignId)).thenReturn(Optional.of(campaign));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(contactRepository.findAllByContactListId(contactListId)).thenReturn(List.of(mock(Contact.class)));

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> sendCampaignUseCase.execute(command));
    }

    @Test
    void shouldThrowExceptionWhenLimitExceeded() {
        // Given
        when(campaign.getOwner()).thenReturn(userId);
        when(campaign.getRecipientList()).thenReturn(contactListId);
        when(user.getPlan()).thenReturn(PlanType.FREE);
        when(campaignRepository.findById(campaignId)).thenReturn(Optional.of(campaign));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(contactRepository.findAllByContactListId(contactListId)).thenReturn(List.of(mock(Contact.class)));
        when(user.canSendMoreEmails(1)).thenReturn(false);

        // When & Then
        assertThrows(ContactListSizeExceedsLimitException.class, () -> sendCampaignUseCase.execute(command));
    }

    @Test
    void shouldHandleEmailSendingException() {
        // Given
        setupReadyToSend();
        when(campaign.getId()).thenReturn(campaignId); // Fix for NPE
        when(campaign.getOwner()).thenReturn(userId);
        when(campaign.getRecipientList()).thenReturn(contactListId);
        when(campaignRepository.findById(campaignId)).thenReturn(Optional.of(campaign));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(contactRepository.findAllByContactListId(contactListId)).thenReturn(List.of(mock(Contact.class)));
        when(user.canSendMoreEmails(1)).thenReturn(true);
        when(campaign.markAsSending()).thenReturn(campaign);
        when(campaign.markAsDraft()).thenReturn(campaign);
        doThrow(new RuntimeException("Sending failed")).when(emailSender).send(campaign);

        // When & Then
        assertThrows(EmailSendingException.class, () -> sendCampaignUseCase.execute(command));
        verify(campaignRepository, times(2)).save(campaign);
    }
}
