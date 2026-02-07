package com.mailboom.api.infrastructure.campaign.persistence.jpa.mapper;

import com.mailboom.api.domain.model.campaign.Campaign;
import com.mailboom.api.domain.model.campaign.valueobjects.*;
import com.mailboom.api.domain.model.contact.valueobjects.ContactListId;
import com.mailboom.api.domain.model.user.valueobjects.UserId;
import com.mailboom.api.infrastructure.campaign.persistence.jpa.entity.CampaignEntity;
import org.springframework.stereotype.Component;

@Component
public class CampaignEntityMapper {

    public CampaignEntity toEntity(com.mailboom.api.domain.model.campaign.Campaign campaign) {
        return CampaignEntity.builder()
                .id(campaign.getId().value())
                .ownerId(campaign.getOwner().value())
                .subject(campaign.getSubject().value())
                .htmlContent(campaign.getHtmlContent().value())
                .senderIdentity(campaign.getSenderIdentity().value())
                .recipientListId(campaign.getRecipientList().value())
                .status(campaign.getStatus().name())
                .createdAt(campaign.getCreatedAt())
                .sentAt(campaign.getSentAt())
                .build();
    }

    public Campaign toDomain(com.mailboom.api.infrastructure.campaign.persistence.jpa.entity.CampaignEntity entity) {
        return Campaign.recreate(
                new CampaignId(entity.getId()),
                new UserId(entity.getOwnerId()),
                new Subject(entity.getSubject()),
                new HtmlContent(entity.getHtmlContent()),
                new EmailSenderIdentity(entity.getSenderIdentity()),
                new ContactListId(entity.getRecipientListId()),
                CampaignStatus.valueOf(entity.getStatus()),
                entity.getCreatedAt(),
                entity.getSentAt()
        );
    }

}
