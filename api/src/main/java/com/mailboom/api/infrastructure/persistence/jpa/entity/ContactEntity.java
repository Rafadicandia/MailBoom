package com.mailboom.api.infrastructure.persistence.jpa.entity;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contact_list_id", nullable = false)
    private ContactListEntity contactListId;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String name;

    @Type(JsonType.class)
    @Column(name = "custom_fields", columnDefinition = "jsonb")
    private Map<String, Object> customFields;

    @Column(nullable = false)
    private boolean subscribed;

}
