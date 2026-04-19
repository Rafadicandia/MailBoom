package com.mailboom.api.infrastructure.whatsapp.persistence.jpa.entity;

import com.mailboom.api.infrastructure.whatsapp.persistence.EncryptionConverter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Entity
@Table(name = "client_config")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientConfigEntity {
    @Id
    private UUID clientId;

    @Column(nullable = false)
    private String wabaId;

    @Column(nullable = false)
    private String phoneNumberId;
    
    //@Convert(converter = EncryptionConverter.class)
    @Column(nullable = false, columnDefinition = "TEXT")
    private String accessToken;
}
