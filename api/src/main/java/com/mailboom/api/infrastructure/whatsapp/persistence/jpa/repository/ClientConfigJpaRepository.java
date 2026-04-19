package com.mailboom.api.infrastructure.whatsapp.persistence.jpa.repository;

import com.mailboom.api.infrastructure.whatsapp.persistence.jpa.entity.ClientConfigEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface ClientConfigJpaRepository extends JpaRepository<ClientConfigEntity, UUID> {
}
