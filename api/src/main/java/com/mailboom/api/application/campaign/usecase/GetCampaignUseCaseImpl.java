package com.mailboom.api.application.campaign.usecase;

import com.mailboom.api.application.campaign.port.in.GetCampaignUseCase;
import com.mailboom.api.application.campaign.usecase.command.GetCampaignCommand;
import com.mailboom.api.domain.model.campaign.Campaign;
import com.mailboom.api.domain.model.campaign.valueobjects.CampaignId;
import com.mailboom.api.domain.port.out.CampaignRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class GetCampaignUseCaseImpl implements GetCampaignUseCase {
    private final CampaignRepository campaignRepository;

    @Override
    public Campaign excecute(GetCampaignCommand command) {
        if (command.id() == null || command.id().isBlank()) {
            throw new IllegalArgumentException("Campaign ID is required");
        }
        CampaignId campaignId = CampaignId.fromString(command.id());
        return campaignRepository.findById(campaignId).orElseThrow(() -> new RuntimeException("Campaign not found"));
    }
}
