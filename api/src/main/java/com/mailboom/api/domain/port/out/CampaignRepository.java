package com.mailboom.api.domain.port.out;

import com.mailboom.api.domain.model.campaign.Campaign;
import com.mailboom.api.domain.model.campaign.valueobjects.CampaignId;
import com.mailboom.api.domain.model.user.valueobjects.UserId;

import java.util.List;

public interface CampaignRepository {
    Campaign save(Campaign campaign);

    void deleteById(CampaignId id);

    Campaign findById(CampaignId id);

    List<Campaign> findAllByOwnerId(UserId ownerId);


}
