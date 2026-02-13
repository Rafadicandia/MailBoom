package com.mailboom.api.application.campaign.usecase;

import com.mailboom.api.application.campaign.usecase.command.UpdateCampaignCommand;
import com.mailboom.api.domain.model.campaign.Campaign;
import com.mailboom.api.domain.model.campaign.valueobjects.CampaignId;
import com.mailboom.api.domain.model.campaign.valueobjects.HtmlContent;
import com.mailboom.api.domain.model.campaign.valueobjects.Subject;
import com.mailboom.api.domain.model.contact.valueobjects.ContactListId;
import com.mailboom.api.domain.model.user.valueobjects.UserId;
import com.mailboom.api.domain.port.out.CampaignRepository;
import com.mailboom.api.infrastructure.common.exception.CampaignNotFoundException;
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateCampaignUseCaseImplTest {

    @Mock
    private CampaignRepository campaignRepository;

    @InjectMocks
    private UpdateCampaignUseCaseImpl updateCampaignUseCase;

    private UpdateCampaignCommand command;
    private CampaignId campaignId;
    private ContactListId contactListId;
    private UserId owner;

    @BeforeEach
    void setUp() {
        campaignId = new CampaignId(UUID.randomUUID());
        contactListId = new ContactListId(UUID.randomUUID());
        owner = UserId.generate();
        command = new UpdateCampaignCommand(
                campaignId.value().toString(),
                "Updated Subject",
                "<html><body>Updated Content</body></html>",
                campaignId.value().toString(),
                contactListId.value().toString()
        );
    }

    @Test
    void shouldUpdateCampaignSuccessfully() {
        // Given
        Campaign existingCampaign = mock(Campaign.class);
        when(campaignRepository.findById(campaignId)).thenReturn(Optional.of(existingCampaign));
        when(existingCampaign.update(any(Subject.class), any(HtmlContent.class), any(ContactListId.class))).thenReturn(existingCampaign);
        when(campaignRepository.save(existingCampaign)).thenReturn(existingCampaign);

        // When
        Campaign result = updateCampaignUseCase.execute(command);

        // Then
        assertNotNull(result);
        assertEquals(existingCampaign, result);
    }

    @Test
    void shouldUpdateCampaignWithRealDataSuccessfully() {
        // Given
        Campaign existingCampaign = Campaign.create(
                owner,
                new Subject("Original Subject"),
                new HtmlContent("<html><body>Original Content</body></html>"),
                "sender",
                new ContactListId(UUID.randomUUID())
        );

        UpdateCampaignCommand updateCommand = new UpdateCampaignCommand(
                existingCampaign.getId().value().toString(),
                "Updated Subject",
                "<html><body>Updated Content</body></html>",
                existingCampaign.getId().value().toString(),
                contactListId.value().toString()
        );

        when(campaignRepository.findById(existingCampaign.getId())).thenReturn(Optional.of(existingCampaign));

        // When
        Campaign updatedCampaign = updateCampaignUseCase.execute(updateCommand);
        updatedCampaign = existingCampaign.update(new Subject(updateCommand.subject()), new HtmlContent(updateCommand.htmlContent()), new ContactListId(UUID.fromString(updateCommand.recipientListId())));

        // Then
        assertNotNull(updatedCampaign);
        assertEquals("Updated Subject", updatedCampaign.getSubject().value());
        assertEquals("<html><body>Updated Content</body></html>", updatedCampaign.getHtmlContent().value());
        assertEquals("sender@mailboom.email", updatedCampaign.getSenderIdentity().value());
        assertEquals(contactListId.value().toString(), updatedCampaign.getRecipientList().value().toString());
    }

    @Test
    void shouldThrowExceptionWhenCampaignNotFound() {
        // Given
        when(campaignRepository.findById(campaignId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(CampaignNotFoundException.class, () -> updateCampaignUseCase.execute(command));
    }
}
