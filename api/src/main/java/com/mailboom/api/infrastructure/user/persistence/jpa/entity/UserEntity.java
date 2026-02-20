package com.mailboom.api.infrastructure.user.persistence.jpa.entity;

import com.mailboom.api.domain.model.user.valueobjects.PlanType;
import com.mailboom.api.domain.model.user.valueobjects.Role;
import com.mailboom.api.infrastructure.campaign.persistence.jpa.entity.CampaignEntity;
import com.mailboom.api.infrastructure.contact.persistence.jpa.entity.ContactListEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
@Data
public class UserEntity {

    @Id
    private UUID id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PlanType plan;

    @Column(name = "emails_sent_this_month", nullable = false)
    private int emailsSentThisMonth;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<TokenEntity> tokens;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ContactListEntity> contactLists;

    @OneToMany(mappedBy = "owner", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<CampaignEntity> campaigns;
}
