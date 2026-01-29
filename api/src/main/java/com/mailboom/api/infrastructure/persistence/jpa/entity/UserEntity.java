package com.mailboom.api.infrastructure.persistence.jpa.entity;

import com.mailboom.api.domain.model.valueobjects.PlanType;
import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Table(name = "users")
@Data
public class UserEntity {

    @Id
    private UUID id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PlanType plan;

    @Column(name = "emails_sent_this_month", nullable = false)
    private int emailsSentThisMonth;
}
