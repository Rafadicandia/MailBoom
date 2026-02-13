package com.mailboom.api.domain.model.campaign;

import com.mailboom.api.domain.model.campaign.valueobjects.*;
import com.mailboom.api.domain.model.contact.valueobjects.ContactListId;
import com.mailboom.api.domain.model.user.valueobjects.UserId;
import lombok.Getter;

import java.time.Instant;

@Getter
public class Campaign {

    private final CampaignId id;
    private final UserId owner;
    private final Subject subject;
    private final HtmlContent htmlContent;
    private final EmailSenderIdentity senderIdentity;
    private final ContactListId recipientList;
    private final CampaignStatus status;
    private final Instant createdAt;
    private final Instant sentAt;

    private Campaign(CampaignId id, UserId owner, Subject subject, HtmlContent htmlContent, EmailSenderIdentity senderIdentity, ContactListId recipientList, CampaignStatus status, Instant createdAt, Instant sentAt) {
        this.id = id;
        this.owner = owner;
        this.subject = subject;
        this.htmlContent = htmlContent;
        this.senderIdentity = senderIdentity;
        this.recipientList = recipientList;
        this.status = status;
        this.createdAt = createdAt;
        this.sentAt = sentAt;
    }

    public static Campaign create(UserId owner, Subject subject, HtmlContent htmlContent, String sender, ContactListId recipientList) {
        return new Campaign(
                CampaignId.newId(),
                owner,
                subject,
                htmlContent,
                formatSenderIdentity(sender),
                recipientList,
                CampaignStatus.DRAFT,
                Instant.now(),
                null
        );
    }

    public static Campaign recreate(CampaignId id, UserId owner, Subject subject, HtmlContent htmlContent, EmailSenderIdentity sender, ContactListId recipientList, CampaignStatus status, Instant createdAt, Instant sentAt) {
        return new Campaign(id, owner, subject, htmlContent, sender, recipientList, status, createdAt, sentAt);
    }

    public Campaign update(Subject subject, HtmlContent htmlContent, ContactListId recipientList) {
        if (this.status != CampaignStatus.DRAFT) {
            throw new IllegalStateException("You can only update a draft campaign.");
        }
        return new Campaign(this.id, this.owner, subject, htmlContent, this.senderIdentity, recipientList, this.status, this.createdAt, this.sentAt);
    }

    public Campaign markAsDraft() {
        return new Campaign(this.id, this.owner, this.subject, this.htmlContent, this.senderIdentity, this.recipientList, CampaignStatus.DRAFT, this.createdAt, null);

    }

    public Campaign markAsSending() {
        if (this.status != CampaignStatus.DRAFT) {
            throw new IllegalStateException("Campaign is not in a draft state.");
        }
        return new Campaign(this.id, this.owner, this.subject, this.htmlContent, this.senderIdentity, this.recipientList, CampaignStatus.SENDING, this.createdAt, null);
    }

    public Campaign markAsSent() {
        if (this.status != CampaignStatus.SENDING) {
            throw new IllegalStateException("Campaign is not in a sending state.");
        }
        return new Campaign(this.id, this.owner, this.subject, this.htmlContent, this.senderIdentity, this.recipientList, CampaignStatus.SENT, this.createdAt, Instant.now());
    }

    public static boolean isReadyToSend(Campaign campaign) {
        return campaign.getStatus() == CampaignStatus.DRAFT && campaign.getRecipientList() != null && campaign.getSenderIdentity() != null && campaign.getSubject() != null && campaign.getHtmlContent() != null;
    }

    public static EmailSenderIdentity formatSenderIdentity(String clientName) {
        return new EmailSenderIdentity(clientName.trim().replace(" ", "") + MailBoomDomain.DOMAIN.domain);
    }


}
