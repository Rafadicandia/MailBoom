package com.mailboom.api.application.admin.usecase;

import com.mailboom.api.application.admin.in.port.GetAllCampaignsUseCase;
import com.mailboom.api.domain.model.campaign.Campaign;
import com.mailboom.api.domain.port.out.CampaignRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GetAllCampaignsUseCaseImpl implements GetAllCampaignsUseCase {
    private final CampaignRepository campaignRepository;

    @Override
    public List<Campaign> execute() {
        return campaignRepository.findAll();
    }
}
