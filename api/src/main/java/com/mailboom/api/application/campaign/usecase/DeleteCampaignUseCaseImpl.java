package com.mailboom.api.application.campaign.usecase;

import com.mailboom.api.application.campaign.port.in.DeleteCampaignUseCase;
import com.mailboom.api.application.campaign.usecase.command.DeleteCampaignCommand;
import com.mailboom.api.domain.model.campaign.valueobjects.CampaignId;
import com.mailboom.api.domain.port.out.CampaignRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DeleteCampaignUseCaseImpl implements DeleteCampaignUseCase {
    private final CampaignRepository campaignRepository;

    @Override
    public void excecute(DeleteCampaignCommand command) {
        CampaignId campaignId = new CampaignId(UUID.fromString(command.campaignId()));
        campaignRepository.findById(campaignId)
                .orElseThrow(() -> new IllegalArgumentException("Campaign with id " + command.campaignId() + " does not exist"));
        campaignRepository.deleteById(campaignId);
    }
}
