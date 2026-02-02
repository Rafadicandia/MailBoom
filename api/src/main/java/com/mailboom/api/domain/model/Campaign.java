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
    private ContactListId recipientList;
    private CampaignStatus status;
    private final Instant createdAt;
    private Instant sentAt;

    private Campaign(CampaignId id, UserId owner, Subject subject, HtmlContent htmlContent, ContactListId recipientList, CampaignStatus status, Instant createdAt, Instant sentAt) {
        this.id = id;
        this.owner = owner;
        this.subject = subject;
        this.htmlContent = htmlContent;
        this.recipientList = recipientList;
        this.status = status;
        this.createdAt = createdAt;
        this.sentAt = sentAt;
    }

    public static Campaign create(UserId owner, Subject subject, HtmlContent htmlContent, ContactListId recipientList) {
        return new Campaign(
                CampaignId.newId(),
                owner,
                subject,
                htmlContent,
                recipientList,
                CampaignStatus.DRAFT,
                Instant.now(),
                null
        );
    }

    public void update(Subject subject, HtmlContent htmlContent, ContactListId recipientList) {
        if (this.status != CampaignStatus.DRAFT) {
            throw new IllegalStateException("Solo se pueden modificar campañas en estado DRAFT.");
        }
        this.subject = subject;
        this.htmlContent = htmlContent;
        this.recipientList = recipientList;
    }

    public void markAsSending() {
        if (this.status != CampaignStatus.DRAFT) {
            throw new IllegalStateException("La campaña ya ha sido enviada o se está enviando.");
        }
        this.status = CampaignStatus.SENDING;
    }

    public void markAsSent() {
        if (this.status != CampaignStatus.SENDING) {
            throw new IllegalStateException("La campaña no se estaba enviando.");
        }
        this.status = CampaignStatus.SENT;
        this.sentAt = Instant.now();
    }
}
