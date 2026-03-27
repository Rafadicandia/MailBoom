package com.mailboom.api.domain.port.out;

import com.mailboom.api.domain.model.whatsapp.Template;
import java.util.Optional;
import java.util.UUID;

public interface TemplateRepository {
    Template save(Template template);
    Optional<Template> findById(UUID id);
}
