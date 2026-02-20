package com.mailboom.api.application.admin.in.port;

import com.mailboom.api.application.campaign.usecase.command.GetCampaignCommand;
import com.mailboom.api.domain.model.campaign.Campaign;
import com.mailboom.api.domain.model.campaign.valueobjects.CampaignId;

public interface GetCampaignDetailsUseCase {
    Campaign execute(GetCampaignCommand command);
}
