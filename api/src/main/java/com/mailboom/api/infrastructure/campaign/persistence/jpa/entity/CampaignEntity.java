package com.mailboom.api.infrastructure.campaign.persistence.jpa.entity;

import com.mailboom.api.infrastructure.user.persistence.jpa.entity.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "campaigns")
@Data
public class CampaignEntity {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private UserEntity owner;

    @Column(nullable = false)
    private String subject;

    @Column(name = "html_content", nullable = false, columnDefinition = "TEXT")
    private String htmlContent;

    @Column(name = "sender_identity", nullable = false)
    private String senderIdentity;

    @Column(name = "recipient_list_id", nullable = false)
    private UUID recipientListId;

    @Column(nullable = false)
    private String status;

    @Column(name = "created_at", nullable = false)
    private java.time.Instant createdAt;

    @Column(name = "sent_at")
    private java.time.Instant sentAt;


}
