package com.mailboom.api.application.campaign.port.in;

import com.mailboom.api.application.campaign.usecase.command.DeleteCampaignCommand;

public interface DeleteCampaignUseCase {
    void excecute(DeleteCampaignCommand command);
}
