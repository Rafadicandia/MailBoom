package com.mailboom.api.application.port.in;

import com.mailboom.api.application.usecase.command.CreateCampaignCommand;
import com.mailboom.api.domain.model.Campaign;

public interface CreateCampaignUseCase {
    Campaign execute(CreateCampaignCommand command);

}
