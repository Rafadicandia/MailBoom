package com.mailboom.api.application.campaign.port.in;

import com.mailboom.api.application.campaign.usecase.command.UpdateCampaignCommand;
import com.mailboom.api.domain.model.campaign.Campaign;

public interface UpdateCampaignUseCase {
    Campaign execute(UpdateCampaignCommand command);
}
