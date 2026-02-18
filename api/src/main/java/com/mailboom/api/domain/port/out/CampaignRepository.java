package com.mailboom.api.domain.port.out;

import com.mailboom.api.domain.model.campaign.Campaign;
import com.mailboom.api.domain.model.campaign.valueobjects.CampaignId;
import com.mailboom.api.domain.model.user.valueobjects.UserId;

import java.util.List;
import java.util.Optional;

public interface CampaignRepository {
    Campaign save(Campaign campaign);

    void deleteById(CampaignId id);

    Optional<Campaign> findById(CampaignId id);

    List<Campaign> findAllByOwnerId(UserId ownerId);

    List<Campaign> findAll();
}
