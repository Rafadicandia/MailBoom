package com.mailboom.api.application.whatsapp.usecase;

import com.mailboom.api.application.whatsapp.port.in.GetAllTemplatesFromUserUseCase;
import com.mailboom.api.application.whatsapp.usecase.command.GetAllTemplatesFromUserCommand;
import com.mailboom.api.domain.model.user.valueobjects.UserId;
import com.mailboom.api.domain.model.whatsapp.Template;
import com.mailboom.api.domain.port.out.TemplateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetAllTemplatesFromUserImpl implements GetAllTemplatesFromUserUseCase {
    private final TemplateRepository templateRepository;

    @Override
    public List<Template> execute(GetAllTemplatesFromUserCommand command) {

        if (command.userId() == null || command.userId().isBlank()) {
            throw new RuntimeException("User id cannot be null nor empty");
        }
        UserId userId = new UserId(UUID.fromString(command.userId()));

        return templateRepository.findAllByOwnerId(userId);

    }

}
