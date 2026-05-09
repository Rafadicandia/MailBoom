package com.mailboom.api.application.whatsapp.port.in;

import com.mailboom.api.application.whatsapp.usecase.command.SendTemplateToListCommand;

public interface SendTemplateToListUseCase {
    void execute(SendTemplateToListCommand command);
}
