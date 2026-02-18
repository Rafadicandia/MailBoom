package com.mailboom.api.infrastructure.campaign.persistence.adapter;

import com.mailboom.api.domain.model.campaign.Campaign;
import com.mailboom.api.domain.model.campaign.valueobjects.CampaignId;
import com.mailboom.api.domain.model.user.valueobjects.UserId;
import com.mailboom.api.domain.port.out.CampaignRepository;
import com.mailboom.api.infrastructure.campaign.persistence.jpa.entity.CampaignEntity;
import com.mailboom.api.infrastructure.campaign.persistence.jpa.mapper.CampaignEntityMapper;
import com.mailboom.api.infrastructure.campaign.persistence.jpa.repository.SpringDataCampaignRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class CampaignRepositoryAdapter implements CampaignRepository {

    private final SpringDataCampaignRepository springDataCampaignRepository;
    private final CampaignEntityMapper campaignEntityMapper;

    @Override
    public Campaign save(Campaign campaign) {
        CampaignEntity entity = campaignEntityMapper.toEntity(campaign);
        CampaignEntity savedEntity = springDataCampaignRepository.save(entity);
        return campaignEntityMapper.toDomain(savedEntity);
    }

    @Override
    public void deleteById(CampaignId id) {
        springDataCampaignRepository.deleteById(id.value());
    }

    @Override
    public Optional<Campaign> findById(CampaignId id) {
        return springDataCampaignRepository.findById(id.value())
                .map(campaignEntityMapper::toDomain);
    }

    @Override
    public List<Campaign> findAllByOwnerId(UserId ownerId) {
        return springDataCampaignRepository.findAllByOwnerId(ownerId.value()).stream()
                .map(campaignEntityMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Campaign> findAll() {
        return springDataCampaignRepository.findAll().stream()
                .map(campaignEntityMapper::toDomain)
                .collect(Collectors.toList());
    }
}
