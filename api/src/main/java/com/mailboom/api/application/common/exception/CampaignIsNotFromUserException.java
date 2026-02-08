package com.mailboom.api.application.common.exception;

public class CampaignIsNotFromUserException extends RuntimeException {
    public CampaignIsNotFromUserException(String message) {
        super(message);
    }
}
