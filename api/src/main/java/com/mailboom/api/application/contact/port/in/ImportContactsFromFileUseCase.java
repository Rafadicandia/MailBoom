package com.mailboom.api.application.contact.port.in;

import com.mailboom.api.application.contact.usecase.command.ImportContactsFromFileCommand;
import com.mailboom.api.domain.model.contact.valueobjects.ContactListId;
import com.mailboom.api.domain.model.user.valueobjects.UserId;

import java.io.InputStream;

public interface ImportContactsFromFileUseCase {
    void execute(ImportContactsFromFileCommand command);
}
