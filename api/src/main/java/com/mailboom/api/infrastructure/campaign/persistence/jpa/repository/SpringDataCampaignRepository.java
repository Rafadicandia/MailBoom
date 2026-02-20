package com.mailboom.api.infrastructure.campaign.persistence.jpa.repository;

import com.mailboom.api.infrastructure.campaign.persistence.jpa.entity.CampaignEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpringDataCampaignRepository extends JpaRepository<CampaignEntity, UUID> {
    List<CampaignEntity> findAllByOwnerId(UUID ownerId);
    List<CampaignEntity> findAll();
}
