package com.mailboom.api.application.admin.usecase;

import com.mailboom.api.application.admin.in.port.GetCampaignDetailsUseCase;
import com.mailboom.api.application.campaign.usecase.command.GetCampaignCommand;
import com.mailboom.api.domain.model.campaign.Campaign;
import com.mailboom.api.domain.model.campaign.valueobjects.CampaignId;
import com.mailboom.api.domain.port.out.CampaignRepository;
import com.mailboom.api.infrastructure.common.exception.CampaignNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class GetCampaignDetailsUseCaseImpl implements GetCampaignDetailsUseCase {
    private final CampaignRepository campaignRepository;

    @Override
    public Campaign execute(GetCampaignCommand command) {
        CampaignId idCampaign = new CampaignId(UUID.fromString(command.id()));
        return campaignRepository.findById(idCampaign)
                .orElseThrow(() -> new CampaignNotFoundException("Campaign not found with id: " + idCampaign.value()));
    }
}
