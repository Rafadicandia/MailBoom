package com.mailboom.api.infrastructure.campaign.awsadapter;

import com.mailboom.api.domain.model.campaign.Campaign;
import com.mailboom.api.domain.model.campaign.valueobjects.CampaignId;
import com.mailboom.api.domain.model.campaign.valueobjects.CampaignStatus;
import com.mailboom.api.domain.model.campaign.valueobjects.EmailSenderIdentity;
import com.mailboom.api.domain.model.campaign.valueobjects.Subject;
import com.mailboom.api.domain.model.common.valueobjects.Email;
import com.mailboom.api.domain.model.campaign.valueobjects.HtmlContent;
import com.mailboom.api.domain.model.common.valueobjects.Name;
import com.mailboom.api.domain.model.contact.Contact;
import com.mailboom.api.domain.model.contact.valueobjects.ContactId;
import com.mailboom.api.domain.model.contact.valueobjects.ContactListId;
import com.mailboom.api.domain.model.user.User;
import com.mailboom.api.domain.model.user.valueobjects.EmailCounter;
import com.mailboom.api.domain.model.user.valueobjects.PasswordHash;
import com.mailboom.api.domain.model.user.valueobjects.UserId;
import com.mailboom.api.domain.port.out.ContactRepository;
import com.mailboom.api.domain.port.out.UserRepository;
import com.mailboom.api.infrastructure.common.exception.EmailSendingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.sesv2.SesV2Client;
import software.amazon.awssdk.services.sesv2.model.SendEmailRequest;
import software.amazon.awssdk.services.sesv2.model.SesV2Exception;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AwsEmailSenderAdapterTest {

    @Mock
    private SesV2Client sesClient;

    @Mock
    private ContactRepository contactRepository;

    @Mock
    private UserRepository userRepository;

    private AwsEmailSenderAdapter emailSender;

    private final String identityArn = "arn:aws:ses:us-east-1:123456789012:identity/example.com";

    @BeforeEach
    void setUp() {
        emailSender = new AwsEmailSenderAdapter(sesClient, contactRepository, userRepository, identityArn);
    }

    @Test
    void shouldTrimWhitespaceFromSenderEmailAddress() {
        // Given
        UserId ownerId = UserId.generate();
        User owner = User.create(ownerId, new Email("owner@example.com"), new Name("Owner"), new PasswordHash("12345678"), new EmailCounter(100));
        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));

        ContactListId contactListId = ContactListId.generate();
        Contact contact = Contact.create(ContactId.generate(), contactListId, new Email("recipient@example.com"), new Name("Recipient"), Collections.emptyMap(), true);
        when(contactRepository.findAllByContactListId(contactListId)).thenReturn(List.of(contact));

        // EmailSenderIdentity solo toma el nombre del cliente y le añade @mailboom.email
        EmailSenderIdentity senderIdentityWithWhitespace = new EmailSenderIdentity(" Test Sender ");
        Campaign campaign = Campaign.recreate(
                CampaignId.newId(),
                ownerId,
                new Subject("Test Subject"),
                new HtmlContent("Test"),
                senderIdentityWithWhitespace,
                contactListId,
                CampaignStatus.DRAFT,
                Instant.now(),
                null
        );

        // When
        emailSender.send(campaign);

        // Then
        ArgumentCaptor<SendEmailRequest> emailRequestCaptor = ArgumentCaptor.forClass(SendEmailRequest.class);
        verify(sesClient).sendEmail(emailRequestCaptor.capture());

        SendEmailRequest capturedRequest = emailRequestCaptor.getValue();
        // El formato esperado es "TestSender@mailboom.email" porque el VO hace trim() y añade el dominio
        assertEquals("TestSender@mailboom.email", capturedRequest.fromEmailAddress());
    }

    @Test
    void shouldSendEmailWithCorrectParameters() {
        // Given
        UserId ownerId = UserId.generate();
        User owner = User.create(ownerId, new Email("owner@example.com"), new Name("Owner"), new PasswordHash("12345678"), new EmailCounter(100));
        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));

        ContactListId contactListId = ContactListId.generate();
        Contact contact = Contact.create(ContactId.generate(), contactListId, new Email("recipient@example.com"), new Name("Recipient"), Collections.emptyMap(), true);
        when(contactRepository.findAllByContactListId(contactListId)).thenReturn(List.of(contact));

        EmailSenderIdentity senderIdentity = new EmailSenderIdentity("TestSender");
        Campaign campaign = Campaign.recreate(
                CampaignId.newId(),
                ownerId,
                new Subject("Test Subject"),
                new HtmlContent("<p>Test</p>"),
                senderIdentity,
                contactListId,
                CampaignStatus.DRAFT,
                Instant.now(),
                null
        );

        // When
        emailSender.send(campaign);

        // Then
        ArgumentCaptor<SendEmailRequest> emailRequestCaptor = ArgumentCaptor.forClass(SendEmailRequest.class);
        verify(sesClient).sendEmail(emailRequestCaptor.capture());

        SendEmailRequest capturedRequest = emailRequestCaptor.getValue();
        assertEquals("TestSender@mailboom.email", capturedRequest.fromEmailAddress());
        assertEquals(identityArn, capturedRequest.fromEmailAddressIdentityArn());
        assertEquals(Collections.singletonList("recipient@example.com"), capturedRequest.destination().bccAddresses());
        assertEquals("Test Subject", capturedRequest.content().simple().subject().data());
    }

    @Test
    void shouldSendEmailsInBatches() {
        // Given
        UserId ownerId = UserId.generate();
        User owner = User.create(ownerId, new Email("owner@example.com"), new Name("Owner"), new PasswordHash("12345678"), new EmailCounter(100));
        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));

        ContactListId contactListId = ContactListId.generate();
        List<Contact> contacts = new ArrayList<>();
        // Create 55 contacts to test batching (batch size is 50)
        for (int i = 0; i < 55; i++) {
            contacts.add(Contact.create(ContactId.generate(), contactListId, new Email("recipient" + i + "@example.com"), new Name("Recipient " + i), Collections.emptyMap(), true));
        }
        when(contactRepository.findAllByContactListId(contactListId)).thenReturn(contacts);

        EmailSenderIdentity senderIdentity = new EmailSenderIdentity("TestSender");
        Campaign campaign = Campaign.recreate(
                CampaignId.newId(),
                ownerId,
                new Subject("Test Subject"),
                new HtmlContent("<p>Test</p>"),
                senderIdentity,
                contactListId,
                CampaignStatus.DRAFT,
                Instant.now(),
                null
        );

        // When
        emailSender.send(campaign);

        // Then
        ArgumentCaptor<SendEmailRequest> emailRequestCaptor = ArgumentCaptor.forClass(SendEmailRequest.class);
        verify(sesClient, times(2)).sendEmail(emailRequestCaptor.capture());

        List<SendEmailRequest> capturedRequests = emailRequestCaptor.getAllValues();
        assertEquals(2, capturedRequests.size());
        assertEquals(50, capturedRequests.get(0).destination().bccAddresses().size());
        assertEquals(5, capturedRequests.get(1).destination().bccAddresses().size());
    }

    @Test
    void shouldThrowExceptionWhenSesClientFails() {
        // Given
        UserId ownerId = UserId.generate();
        User owner = User.create(ownerId, new Email("owner@example.com"), new Name("Owner"), new PasswordHash("12345678"), new EmailCounter(100));
        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));

        ContactListId contactListId = ContactListId.generate();
        Contact contact = Contact.create(ContactId.generate(), contactListId, new Email("recipient@example.com"), new Name("Recipient"), Collections.emptyMap(), true);
        when(contactRepository.findAllByContactListId(contactListId)).thenReturn(List.of(contact));

        EmailSenderIdentity senderIdentity = new EmailSenderIdentity("TestSender");
        Campaign campaign = Campaign.recreate(
                CampaignId.newId(),
                ownerId,
                new Subject("Test Subject"),
                new HtmlContent("<p>Test</p>"),
                senderIdentity,
                contactListId,
                CampaignStatus.DRAFT,
                Instant.now(),
                null
        );

        when(sesClient.sendEmail(any(SendEmailRequest.class))).thenThrow(SesV2Exception.builder().message("SES Error").build());

        // When & Then
        assertThrows(EmailSendingException.class, () -> emailSender.send(campaign));
    }
}
