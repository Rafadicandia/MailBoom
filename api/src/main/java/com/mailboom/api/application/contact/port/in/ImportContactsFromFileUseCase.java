package com.mailboom.api.application.contact.port.in;

import com.mailboom.api.domain.model.valueobjects.ContactListId;
import com.mailboom.api.domain.model.valueobjects.UserId;

import java.io.InputStream;

public interface ImportContactsFromFileUseCase {
    void execute(ContactListId listId, UserId ownerId, InputStream fileStream, String contentType);
}
