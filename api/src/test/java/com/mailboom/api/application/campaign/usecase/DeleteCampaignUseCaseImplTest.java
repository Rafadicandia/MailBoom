package com.mailboom.api.application.campaign.usecase;

import com.mailboom.api.application.campaign.usecase.command.DeleteCampaignCommand;
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

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteCampaignUseCaseImplTest {

    @Mock
    private CampaignRepository campaignRepository;

    @InjectMocks
    private DeleteCampaignUseCaseImpl deleteCampaignUseCase;

    private DeleteCampaignCommand command;
    private CampaignId campaignId;

    @BeforeEach
    void setUp() {
        campaignId = new CampaignId(UUID.randomUUID());
        command = new DeleteCampaignCommand(campaignId.value().toString());
    }

    @Test
    void shouldDeleteCampaignSuccessfully() {
        // Given
        when(campaignRepository.findById(campaignId)).thenReturn(Optional.of(mock(Campaign.class)));

        // When
        deleteCampaignUseCase.excecute(command);

        // Then
        verify(campaignRepository).deleteById(campaignId);
    }

    @Test
    void shouldThrowExceptionWhenCampaignNotFound() {
        // Given
        when(campaignRepository.findById(campaignId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> deleteCampaignUseCase.excecute(command));
    }
}
