package com.mailboom.api.infrastructure.contact.dto;

public record ImportFromFileRequest(
        String listId,
        String ownerId,
        String file,
        String contentType

) {
    public ImportFromFileRequest {
        if (listId == null) {
            throw new IllegalArgumentException("List ID cannot be null");
        }
        if (ownerId == null) {
            throw new IllegalArgumentException("Owner ID cannot be null");
        }
        if (file == null) {
            throw new IllegalArgumentException("File cannot be null");
        }
        if (contentType == null) {
            throw new IllegalArgumentException("Content type cannot be null");
        }
    }
}
