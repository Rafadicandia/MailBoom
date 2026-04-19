package com.mailboom.api.infrastructure.whatsapp.persistence.jpa.repository;

import com.mailboom.api.infrastructure.whatsapp.persistence.jpa.entity.TemplateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface JpaTemplateRepository extends JpaRepository<TemplateEntity, UUID> {
}
