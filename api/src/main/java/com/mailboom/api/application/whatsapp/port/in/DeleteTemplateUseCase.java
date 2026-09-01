package com.mailboom.api.application.whatsapp.port.in;

import com.mailboom.api.application.whatsapp.usecase.command.DeleteTemplateCommand;


public interface DeleteTemplateUseCase {
    void delete(DeleteTemplateCommand command);
}
