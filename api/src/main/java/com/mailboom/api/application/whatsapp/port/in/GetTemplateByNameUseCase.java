package com.mailboom.api.application.whatsapp.port.in;

import com.mailboom.api.application.whatsapp.usecase.command.GetTemplateByNameCommand;
import com.mailboom.api.domain.model.whatsapp.Template;

public interface GetTemplateByNameUseCase {
    Template execute(GetTemplateByNameCommand command);
}
