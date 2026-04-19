package com.mailboom.api.application.whatsapp.usecase;

import com.mailboom.api.application.whatsapp.port.in.GetTemplateByNameUseCase;
import com.mailboom.api.application.whatsapp.usecase.command.GetTemplateByNameCommand;
import com.mailboom.api.domain.model.whatsapp.Template;
import com.mailboom.api.domain.port.out.TemplateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetTemplateByNameUseCaseImpl implements GetTemplateByNameUseCase {

    private final TemplateRepository templateRepository;

    @Override
    @Transactional(readOnly = true)
    public Template execute(GetTemplateByNameCommand command) {

        return templateRepository.findByName(command.templateName()).orElseThrow(() -> new RuntimeException("Template not found: " + command.templateName()));
    }
}
