package com.mailboom.api.application.whatsapp.port.in;

import com.mailboom.api.application.whatsapp.usecase.command.SaveClientConfigCommand;
import com.mailboom.api.domain.model.whatsapp.ClientConfig;

public interface SaveClientConfigUseCase {
    ClientConfig save(SaveClientConfigCommand command);
}
