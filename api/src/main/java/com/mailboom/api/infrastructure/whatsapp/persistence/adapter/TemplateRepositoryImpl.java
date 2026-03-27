package com.mailboom.api.infrastructure.whatsapp.persistence.adapter;


import com.mailboom.api.domain.model.whatsapp.Template;
import com.mailboom.api.domain.port.out.TemplateRepository;
import com.mailboom.api.infrastructure.whatsapp.persistence.jpa.entity.TemplateEntity;
import com.mailboom.api.infrastructure.whatsapp.persistence.jpa.mapper.TemplateEntityMapper;
import com.mailboom.api.infrastructure.whatsapp.persistence.jpa.repository.JpaTemplateRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class TemplateRepositoryImpl implements TemplateRepository {

    private final JpaTemplateRepository jpaTemplateRepository;
    private final TemplateEntityMapper mapper;

    public TemplateRepositoryImpl(JpaTemplateRepository jpaTemplateRepository, TemplateEntityMapper mapper) {
        this.jpaTemplateRepository = jpaTemplateRepository;
        this.mapper = mapper;
    }

    @Override
    public Template save(Template template) {
        TemplateEntity entity = mapper.toEntity(template);
        TemplateEntity savedEntity = jpaTemplateRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Template> findById(UUID id) {
        return jpaTemplateRepository.findById(id).map(mapper::toDomain);
    }
}
