package com.mailboom.api.application.whatsapp.port.in;

import com.mailboom.api.application.whatsapp.usecase.command.GetAllTemplatesFromUserCommand;
import com.mailboom.api.domain.model.whatsapp.Template;

import java.util.List;

public interface GetAllTemplatesFromUserUseCase {
    List<Template> execute(GetAllTemplatesFromUserCommand command);
}
