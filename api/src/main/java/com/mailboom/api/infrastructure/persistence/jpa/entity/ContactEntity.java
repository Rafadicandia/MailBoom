package com.mailboom.api.infrastructure.persistence.jpa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "contacts")
@Data
public class ContactEntity {

    @Id
    private UUID id;

    @ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
    @JoinColumn(name = "contact_list_id", nullable = false)
    private ContactListEntity contactListId;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String name;

    @ElementCollection
    @CollectionTable(name = "contact_custom_fields", joinColumns = @JoinColumn(name = "contact_id"))
    @MapKeyColumn(name = "field_name")
    @Column(name = "field_value")
    private Map<String, Object> customFields;

    @Column(nullable = false)
    private boolean subscribed;

}
