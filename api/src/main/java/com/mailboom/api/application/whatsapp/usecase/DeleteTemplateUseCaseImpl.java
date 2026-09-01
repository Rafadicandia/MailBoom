package com.mailboom.api.application.whatsapp.usecase;

import com.mailboom.api.application.whatsapp.port.in.DeleteTemplateUseCase;
import com.mailboom.api.application.whatsapp.usecase.command.DeleteTemplateCommand;
import com.mailboom.api.domain.port.out.TemplateRepository;
import lombok.AllArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
public class DeleteTemplateUseCaseImpl implements DeleteTemplateUseCase {
    private final TemplateRepository templateRepository;

    @Override
    public void delete(DeleteTemplateCommand command) {
        UUID templateId = command.templateId();
        templateRepository.delete(templateId);
    }

}
