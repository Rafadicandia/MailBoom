package com.mailboom.api.infrastructure.whatsapp.persistence.jpa.entity;


import com.mailboom.api.domain.model.whatsapp.Template;
import com.mailboom.api.domain.model.whatsapp.valueobjects.TemplateStatus;
import com.mailboom.api.infrastructure.user.persistence.jpa.entity.UserEntity;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Entity
@Table(name = "templates")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TemplateEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(name = "category")
    private String category;

    @Column(nullable = false)
    private String parameterFormat;

    @Type(JsonType.class)
    @Column(name = "components", columnDefinition = "jsonb")
    private Map<String, Object> components;

    @Column(name = "language")
    private String language;

    @Column(name = "status")
    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity ownerId;

}


