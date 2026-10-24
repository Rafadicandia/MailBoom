package com.mailboom.api.application.whatsapp.usecase;

import com.mailboom.api.application.whatsapp.port.in.UpdateTemplateUseCase;
import com.mailboom.api.application.whatsapp.usecase.command.UpdateTemplateMessageCommand;
import com.mailboom.api.domain.model.user.valueobjects.UserId;
import com.mailboom.api.domain.model.whatsapp.Template;
import com.mailboom.api.domain.model.whatsapp.valueobjects.*;
import com.mailboom.api.domain.port.out.TemplateRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class UpdateTemplateUseCaseImpl implements UpdateTemplateUseCase {

    private final TemplateRepository templateRepository;

    @Override
    public void execute(UpdateTemplateMessageCommand command) {

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

        Template template = Template.create(
                command.templateId(),
                command.name(),
                Category.valueOf(command.category()),
                ParameterFormat.valueOf(command.parameterFormat()),
                command.components(),
                Languajes.fromCode(command.language()),
                TemplateStatus.PENDING,
                userId
        );

        templateRepository.save(template);
    }
}
