package com.mailboom.api.application.campaign.port.in;

import com.mailboom.api.application.campaign.usecase.command.SendCampaignCommand;

public interface SendCampaignUseCase {
    void execute(SendCampaignCommand command);
}
