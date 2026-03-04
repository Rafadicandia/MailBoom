package com.mailboom.api.domain.exception;

public class WhatsAppCampaignNotFoundException extends RuntimeException {

    public WhatsAppCampaignNotFoundException(String message) {
        super(message);
    }

    public WhatsAppCampaignNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
