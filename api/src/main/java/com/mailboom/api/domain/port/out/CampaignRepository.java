package com.mailboom.api.domain.port.out;

import com.mailboom.api.domain.model.Campaign;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CampaignRepository {
    Optional<Campaign> findById(UUID id);
    List<Campaign> findAllByUserId(UUID userId);
    Campaign save(Campaign campaign);
    void deleteById(UUID id);
}
