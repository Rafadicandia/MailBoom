package com.mailboom.api.application.whatsapp.port.in;

import com.mailboom.api.application.whatsapp.usecase.command.CreateTemplateCommand;
import com.mailboom.api.domain.model.whatsapp.Template;

public interface CreateTemplateUseCase {
    Template createTemplate(CreateTemplateCommand command);
}
