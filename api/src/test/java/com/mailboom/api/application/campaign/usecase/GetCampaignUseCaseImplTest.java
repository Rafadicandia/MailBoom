package com.mailboom.api.application.campaign.usecase;

import com.mailboom.api.application.campaign.usecase.command.GetCampaignCommand;
import com.mailboom.api.domain.model.campaign.Campaign;
import com.mailboom.api.domain.model.campaign.valueobjects.CampaignId;
import com.mailboom.api.domain.port.out.CampaignRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetCampaignUseCaseImplTest {

    @Mock
    private CampaignRepository campaignRepository;

    @InjectMocks
    private GetCampaignUseCaseImpl getCampaignUseCase;

    private CampaignId campaignId;

    @BeforeEach
    void setUp() {
        campaignId = new CampaignId(UUID.randomUUID());
    }

    @Test
    void shouldGetCampaignSuccessfully() {
        // Given
        GetCampaignCommand command = new GetCampaignCommand(campaignId.value().toString());
        Campaign campaign = mock(Campaign.class);
        when(campaignRepository.findById(campaignId)).thenReturn(Optional.of(campaign));

        // When
        Campaign result = getCampaignUseCase.excecute(command);

        // Then
        assertNotNull(result);
        assertEquals(campaign, result);
    }

    @Test
    void shouldThrowExceptionWhenCampaignNotFound() {
        // Given
        GetCampaignCommand command = new GetCampaignCommand(campaignId.value().toString());
        when(campaignRepository.findById(campaignId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> getCampaignUseCase.excecute(command));
    }

    @Test
    void shouldThrowExceptionWhenCampaignIdIsBlank() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> new GetCampaignCommand(""));
    }
}
