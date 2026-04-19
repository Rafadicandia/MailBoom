package com.mailboom.api.application.whatsapp.usecase;

import com.mailboom.api.application.whatsapp.port.in.CreateTemplateUseCase;
import com.mailboom.api.application.whatsapp.usecase.command.CreateTemplateCommand;
import com.mailboom.api.domain.model.user.valueobjects.UserId;
import com.mailboom.api.domain.model.whatsapp.ClientConfig;
import com.mailboom.api.domain.model.whatsapp.Template;
import com.mailboom.api.domain.model.whatsapp.valueobjects.AccessToken;
import com.mailboom.api.domain.model.whatsapp.valueobjects.Category;
import com.mailboom.api.domain.model.whatsapp.valueobjects.Languajes;
import com.mailboom.api.domain.model.whatsapp.valueobjects.ParameterFormat;
import com.mailboom.api.domain.model.whatsapp.valueobjects.TemplateStatus;
import com.mailboom.api.domain.port.out.ClientConfigRepository;
import com.mailboom.api.domain.port.out.TemplateRepository;
import com.mailboom.api.domain.port.out.WhatsAppGateway;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@AllArgsConstructor
public class CreateTemplateUseCaseImpl implements CreateTemplateUseCase {

    private final TemplateRepository templateRepository;
    private final WhatsAppGateway whatsAppGateway;
    private final ClientConfigRepository clientConfigRepository;

    @Override
    public Template execute(CreateTemplateCommand command) {

        UserId userId = new UserId(command.ownerId());
        ClientConfig config = clientConfigRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Configuration not found for user: " + userId.value()));

        Template template = new Template(
            UUID.randomUUID(),
            command.name(),
            Category.valueOf(command.category()),
            ParameterFormat.valueOf(command.parameterFormat()),
            command.components(),
            Languajes.fromCode(command.language()),
            TemplateStatus.APPROVED,
            userId
        );

        Template savedTemplate = templateRepository.save(template);
        whatsAppGateway.sendTemplateForReview(savedTemplate, config);
        return savedTemplate;
    }
}
