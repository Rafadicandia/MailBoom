package com.mailboom.api.application.admin.in.port;

import com.mailboom.api.domain.model.campaign.Campaign;
import java.util.List;

public interface GetAllCampaignsUseCase {
    List<Campaign> execute();
}
