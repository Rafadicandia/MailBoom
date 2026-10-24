package com.mailboom.api.application.whatsapp.port.in;


import com.mailboom.api.application.whatsapp.usecase.command.UpdateTemplateMessageCommand;

public interface UpdateTemplateUseCase {
    void execute(UpdateTemplateMessageCommand command);
}
