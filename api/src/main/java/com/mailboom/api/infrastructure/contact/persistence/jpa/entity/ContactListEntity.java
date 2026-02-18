package com.mailboom.api.infrastructure.contact.persistence.jpa.entity;

import com.mailboom.api.infrastructure.user.persistence.jpa.entity.UserEntity;
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
@Table(name = "contact_list")
@Data
public class ContactListEntity {

    @Id
    private UUID id;

    @ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(nullable = false)
    private String name;

    @Column(name = "total_contacts", nullable = false)
    private long totalContacts;

    @OneToMany(mappedBy = "contactListId", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ContactEntity> contacts;
}
