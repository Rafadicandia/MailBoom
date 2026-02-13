package com.mailboom.api.application.campaign.usecase;

import com.mailboom.api.application.campaign.port.in.CreateCampaignUseCase;
import com.mailboom.api.application.campaign.usecase.command.CreateCampaignCommand;
import com.mailboom.api.domain.model.campaign.Campaign;
import com.mailboom.api.domain.model.campaign.valueobjects.HtmlContent;
import com.mailboom.api.domain.model.campaign.valueobjects.Subject;
import com.mailboom.api.domain.model.contact.valueobjects.ContactListId;
import com.mailboom.api.domain.model.user.valueobjects.UserId;
import com.mailboom.api.domain.port.out.CampaignRepository;
import com.mailboom.api.domain.port.out.UserRepository;
import lombok.AllArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@AllArgsConstructor
public class CreateCampaignUseCaseImpl implements CreateCampaignUseCase {

    private final CampaignRepository campaignRepository;
    private final UserRepository userRepository;

    public static String validateContent(String content) {
        return Jsoup.clean(content, Safelist.relaxed());
    }

    @Override
    public Campaign execute(CreateCampaignCommand command) {
        UserId userId = new UserId(UUID.fromString(command.ownerId()));
        userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String content = validateContent(command.htmlContent());
        Campaign campaign = Campaign.create(
                userId,
                new Subject(command.subject()),
                new HtmlContent(content),
                command.sender(),
                new ContactListId(UUID.fromString(command.recipientListId()))
        );
        return campaignRepository.save(campaign);
    }
}
