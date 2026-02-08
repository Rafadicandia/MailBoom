package com.mailboom.api.application.campaign.usecase;

import com.mailboom.api.application.campaign.port.in.DeleteCampaignUseCase;
import com.mailboom.api.application.campaign.usecase.command.DeleteCampaignCommand;
import com.mailboom.api.application.common.exception.CampaignIsNotFromUserException;
import com.mailboom.api.domain.model.campaign.valueobjects.CampaignId;
import com.mailboom.api.domain.model.user.valueobjects.UserId;
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
        if (campaignRepository.findById(campaignId) == null) {
            throw new IllegalArgumentException("Campaign with id " + command.campaignId() + " does not exist");

        }
        campaignRepository.deleteById(campaignId);

    }
}
