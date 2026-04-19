package com.mailboom.api.application.whatsapp.usecase;

import com.mailboom.api.application.whatsapp.port.in.SendTemplateMessageUseCase;
import com.mailboom.api.application.whatsapp.usecase.command.SendTemplateMessageCommand;
import com.mailboom.api.domain.model.common.valueobjects.Phone;
import com.mailboom.api.domain.model.whatsapp.ClientConfig;
import com.mailboom.api.domain.model.whatsapp.Template;
import com.mailboom.api.domain.model.whatsapp.valueobjects.TemplateStatus;
import com.mailboom.api.domain.port.out.ClientConfigRepository;
import com.mailboom.api.domain.port.out.TemplateRepository;
import com.mailboom.api.domain.port.out.WhatsAppGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SendTemplateMessageUseCaseImpl implements SendTemplateMessageUseCase {

    private final TemplateRepository templateRepository;
    private final ClientConfigRepository clientConfigRepository;
    private final WhatsAppGateway whatsAppGateway;

    @Override
    @Transactional(readOnly = true)
    public void execute(SendTemplateMessageCommand command) {
        Phone to = Phone.fromString(command.to());
        Template template = templateRepository.findById(command.templateId())
                .orElseThrow(() -> new RuntimeException("Template not found: " + command.templateId()));

//        if (template.getStatus() != TemplateStatus.APPROVED) {
//            // Nota: Podrías querer crear una excepción personalizada TemplateNotApprovedException
//            throw new RuntimeException("Template '" + template.getName() + "' is not approved yet. Current status: " + template.getStatus());
//        }

        ClientConfig config = clientConfigRepository.findById(template.getOwnerId())
                .orElseThrow(() -> new RuntimeException("WhatsApp configuration not found for user: " + template.getOwnerId().value()));

        whatsAppGateway.sendTemplateMessage(to, template, command.parameters(), config);
    }
}
