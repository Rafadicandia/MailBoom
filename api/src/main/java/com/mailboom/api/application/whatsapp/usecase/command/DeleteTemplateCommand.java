package com.mailboom.api.application.whatsapp.usecase.command;

import java.util.UUID;

public record DeleteTemplateCommand(
        UUID templateId
) {
    public DeleteTemplateCommand{
        if(templateId == null){
            throw new IllegalArgumentException("Template Id cant't be null");
        }
    }
}
