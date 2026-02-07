package com.mailboom.api.application.campaign.port.in;

import com.mailboom.api.application.campaign.usecase.command.CreateCampaignCommand;
import com.mailboom.api.domain.model.campaign.Campaign;

public interface CreateCampaignUseCase {
    Campaign execute(CreateCampaignCommand command);
}

