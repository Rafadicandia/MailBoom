package com.mailboom.api.application.campaign.usecase;

import com.mailboom.api.application.campaign.port.in.UpdateCampaignUseCase;
import com.mailboom.api.application.campaign.usecase.command.UpdateCampaignCommand;
import com.mailboom.api.domain.model.campaign.Campaign;
import com.mailboom.api.domain.model.campaign.valueobjects.CampaignId;
import com.mailboom.api.domain.model.campaign.valueobjects.HtmlContent;
import com.mailboom.api.domain.model.campaign.valueobjects.Subject;
import com.mailboom.api.domain.model.contact.valueobjects.ContactListId;
import com.mailboom.api.domain.port.out.CampaignRepository;
import com.mailboom.api.infrastructure.common.exception.CampaignNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@AllArgsConstructor
public class UpdateCampaignUseCaseImpl implements UpdateCampaignUseCase {

    private final CampaignRepository campaignRepository;

    @Override
    public Campaign execute(UpdateCampaignCommand command) {

        CampaignId campaignId = CampaignId.fromString(command.id());

        Campaign existingCampaign = campaignRepository.findById(campaignId)
                .orElseThrow(() -> new CampaignNotFoundException("Campaign with id " + command.id() + " not found."));

        Campaign updatedCampaign = existingCampaign.update(
                new Subject(command.subject()),
                new HtmlContent(command.htmlContent()),
                new ContactListId(UUID.fromString(command.recipientListId()))
        );

        return campaignRepository.save(updatedCampaign);
    }
}
