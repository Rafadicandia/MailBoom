package com.mailboom.api.application.campaign.usecase;

import com.mailboom.api.application.campaign.port.in.SendCampaignUseCase;
import com.mailboom.api.application.campaign.usecase.command.SendCampaignCommand;
import com.mailboom.api.application.common.exception.ContactListSizeExceedsLimitException;
import com.mailboom.api.domain.model.campaign.Campaign;
import com.mailboom.api.domain.model.campaign.valueobjects.CampaignId;
import com.mailboom.api.domain.model.contact.Contact;
import com.mailboom.api.domain.model.user.User;
import com.mailboom.api.domain.model.user.valueobjects.UserId;
import com.mailboom.api.domain.port.out.CampaignRepository;
import com.mailboom.api.domain.port.out.ContactRepository;
import com.mailboom.api.domain.port.out.EmailSender;
import com.mailboom.api.domain.port.out.UserRepository;
import com.mailboom.api.infrastructure.common.exception.EmailSendingException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@AllArgsConstructor
public class SendCampaignUseCaseImpl implements SendCampaignUseCase {
    private final EmailSender emailSender;
    private final UserRepository userRepository;
    private final CampaignRepository campaignRepository;
    private final ContactRepository contactRepository;

    @Override
    public void execute(SendCampaignCommand command) {
        UserId userId = new UserId(UUID.fromString(command.ownerId()));
        CampaignId campaignId = new CampaignId(UUID.fromString(command.campaignId()));
        var campaign = campaignRepository.findById(campaignId).orElseThrow(() -> new IllegalArgumentException("Campaign not found"));
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        List<Contact> totalEmailsInList = contactRepository.findAllByContactListId(campaign.getRecipientList());

        //validamos si el usuario existe
        if (userRepository.findById(userId).isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }
        //validamos si es el dueno de la campana
        if (!campaign.getOwner().equals(userId)) {
            throw new IllegalArgumentException("You are not the owner of this campaign");
        }
        //validamos sipuede enviar mas mailsporque esa dentro de los limites
        if (!user.canSendMoreEmails(totalEmailsInList.size())) {
            throw new ContactListSizeExceedsLimitException("The current plan does not allow sending more emails than: " + user.getPlan().getEmailLimit() + ". Your list size has: " + totalEmailsInList.size() + " emails");

        }
        if (Campaign.isReadyToSend(campaign)) {
            campaign = campaign.markAsSending();
            campaignRepository.save(campaign);
        }
        try {
            emailSender.send(campaign);
            campaign = campaign.markAsSent();
            campaignRepository.save(campaign);

        } catch (RuntimeException e) {
            campaign = campaign.markAsDraft();
            campaignRepository.save(campaign);
            throw new EmailSendingException("Error sending email for campaign " + campaign.getId().value() + ": " + e.getMessage());
        }

        User userEmailsCountUpdate = user.incrementEmailsSent(totalEmailsInList.size());
        userRepository.save(userEmailsCountUpdate);

    }
}
