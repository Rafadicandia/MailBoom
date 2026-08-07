package com.mailboom.api.application.whatsapp.usecase;

import com.mailboom.api.application.whatsapp.port.in.CreateTemplateUseCase;
import com.mailboom.api.application.whatsapp.usecase.command.CreateTemplateCommand;
import com.mailboom.api.domain.model.user.valueobjects.UserId;
import com.mailboom.api.domain.model.whatsapp.ClientConfig;
import com.mailboom.api.domain.model.whatsapp.Template;
import com.mailboom.api.domain.model.whatsapp.valueobjects.AccessToken;
import com.mailboom.api.domain.model.whatsapp.valueobjects.Category;
import com.mailboom.api.domain.model.whatsapp.valueobjects.ComponentType;
import com.mailboom.api.domain.model.whatsapp.valueobjects.Languajes;
import com.mailboom.api.domain.model.whatsapp.valueobjects.ParameterFormat;
import com.mailboom.api.domain.model.whatsapp.valueobjects.TemplateComponent;
import com.mailboom.api.domain.model.whatsapp.valueobjects.TemplateStatus;
import com.mailboom.api.domain.port.out.ClientConfigRepository;
import com.mailboom.api.domain.port.out.TemplateRepository;
import com.mailboom.api.domain.port.out.WhatsAppGateway;
import com.mailboom.api.infrastructure.whatsapp.dto.MetaTemplateResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@AllArgsConstructor
@Slf4j
public class CreateTemplateUseCaseImpl implements CreateTemplateUseCase {

    private final TemplateRepository templateRepository;
    private final WhatsAppGateway whatsAppGateway;
    private final ClientConfigRepository clientConfigRepository;

    @Override
    public Template execute(CreateTemplateCommand command) {

        // Validate components before sending to Meta API
        if (command.components() != null) {
            for (TemplateComponent component : command.components()) {
                if (component.type() == ComponentType.BUTTONS) {
                    if (component.buttons() == null || component.buttons().isEmpty()) {
                        throw new IllegalArgumentException("BUTTONS component must have at least one button defined");
                    }
                }
            }
        }

        UserId userId = new UserId(command.ownerId());
        ClientConfig config = clientConfigRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Configuration not found for user: " + userId.value()));

        Template template = Template.create(
            UUID.randomUUID(),
            command.name(),
            Category.valueOf(command.category()),
            ParameterFormat.valueOf(command.parameterFormat()),
            command.components(),
            Languajes.fromCode(command.language()),
            TemplateStatus.PENDING,
            userId
        );

        Template savedTemplate = templateRepository.save(template);

        MetaTemplateResponse metaResponse = whatsAppGateway.sendTemplateForReview(savedTemplate, config);

        TemplateStatus metaStatus = TemplateStatus.PENDING;
        if (metaResponse != null && metaResponse.status() != null) {
            try {
                metaStatus = TemplateStatus.valueOf(metaResponse.status().toUpperCase());
            } catch (IllegalArgumentException e) {
                log.warn("Unknown Meta template status: {}", metaResponse.status());
            }
        }

        savedTemplate.updateMetaInfo(
            metaResponse != null ? metaResponse.id() : null,
            metaStatus
        );

        return templateRepository.save(savedTemplate);
    }
}
