package com.mailboom.api.application.campaign.port.in;

import com.mailboom.api.application.campaign.usecase.command.GetCampaignCommand;
import com.mailboom.api.domain.model.campaign.Campaign;

public interface GetCampaignUseCase {
    Campaign excecute(GetCampaignCommand command);
}
