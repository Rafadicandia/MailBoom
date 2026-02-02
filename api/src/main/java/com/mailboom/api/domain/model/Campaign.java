package com.mailboom.api.domain.model;

import com.mailboom.api.domain.model.valueobjects.*;
import lombok.Getter;

import java.time.Instant;

@Getter
public class Campaign {

    private final CampaignId id;
    private final UserId owner;
    private Subject subject;
    private HtmlContent htmlContent;
    private EmailSenderIdentity senderIdentity;
    private ContactListId recipientList;
    private CampaignStatus status;
    private final Instant createdAt;
    private Instant sentAt;

    private Campaign(CampaignId id, UserId owner, Subject subject, HtmlContent htmlContent,  EmailSenderIdentity senderIdentity, ContactListId recipientList, CampaignStatus status, Instant createdAt, Instant sentAt) {
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
                new EmailSenderIdentity(sender),
                recipientList,
                CampaignStatus.DRAFT,
                Instant.now(),
                null
        );
    }

    public void update(Subject subject, HtmlContent htmlContent, ContactListId recipientList) {
        if (this.status != CampaignStatus.DRAFT) {
            throw new IllegalStateException("You can only update a draft campaign.");
        }
        this.subject = subject;
        this.htmlContent = htmlContent;
        this.recipientList = recipientList;
    }

    public void markAsSending() {
        if (this.status != CampaignStatus.DRAFT) {
            throw new IllegalStateException("Campaign is not in a draft state.");
        }
        this.status = CampaignStatus.SENDING;
    }

    public void markAsSent() {
        if (this.status != CampaignStatus.SENDING) {
            throw new IllegalStateException("Campaign is not in a sending state.");
        }
        this.status = CampaignStatus.SENT;
        this.sentAt = Instant.now();
    }

    public String getFullSenderName() {
        return senderIdentity.format();
    }
}
