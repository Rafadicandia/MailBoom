package com.mailboom.api.application.campaign.port.in;

import com.mailboom.api.application.campaign.usecase.command.GetSentCampaignsFromUserCommand;
import com.mailboom.api.domain.model.campaign.Campaign;

import java.util.List;

public interface GetSentCampaignsFromUserUseCase {
    List<Campaign> execute(GetSentCampaignsFromUserCommand command);

}
