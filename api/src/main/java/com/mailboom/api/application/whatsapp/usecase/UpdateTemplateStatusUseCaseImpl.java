package com.mailboom.api.application.whatsapp.usecase;

import com.mailboom.api.application.whatsapp.port.in.UpdateTemplateStatusUseCase;

import com.mailboom.api.domain.model.whatsapp.Template;
import com.mailboom.api.domain.model.whatsapp.valueobjects.TemplateStatus;
import com.mailboom.api.domain.port.out.TemplateRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@AllArgsConstructor
public class UpdateTemplateStatusUseCaseImpl implements UpdateTemplateStatusUseCase {

    private final TemplateRepository templateRepository;

    @Override
    public void updateStatus(UUID templateId, TemplateStatus status) {
        Template template = templateRepository.findById(templateId)
            .orElseThrow(() -> new RuntimeException("Template not found"));
        
        template.setStatus(status);
        templateRepository.save(template);
    }
}
