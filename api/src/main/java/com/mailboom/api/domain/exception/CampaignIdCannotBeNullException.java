package com.mailboom.api.domain.exception;

public class CampaignIdCannotBeNullException extends RuntimeException {
    public CampaignIdCannotBeNullException(String message) {
        super(message);
    }
}
