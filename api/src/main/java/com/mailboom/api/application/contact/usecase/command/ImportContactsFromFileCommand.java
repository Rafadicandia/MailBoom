package com.mailboom.api.application.contact.usecase.command;

import java.io.InputStream;

public record ImportContactsFromFileCommand(
        String listId,
        String ownerId,
        InputStream fileStream,
        String contentType) {
    public ImportContactsFromFileCommand {
        if (listId == null) {
            throw new IllegalArgumentException("List ID cannot be null");
        }
        if (ownerId == null) {
            throw new IllegalArgumentException("Owner ID cannot be null");
        }
        if (fileStream == null) {
            throw new IllegalArgumentException("File stream cannot be null");
        }
        if (contentType == null) {
            throw new IllegalArgumentException("Content type cannot be null");
        }
    }
}
