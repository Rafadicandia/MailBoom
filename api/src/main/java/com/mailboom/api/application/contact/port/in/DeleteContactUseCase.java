package com.mailboom.api.application.contact.port.in;

import com.mailboom.api.application.contact.usecase.command.DeleteContactCommand;

public interface DeleteContactUseCase {
    void execute(DeleteContactCommand command);
}
