package com.mailboom.api.application.contact.port.in;

import com.mailboom.api.application.contact.usecase.command.DeleteContactListCommand;

public interface DeleteContactListUseCase {
    void execute(DeleteContactListCommand command);

}
