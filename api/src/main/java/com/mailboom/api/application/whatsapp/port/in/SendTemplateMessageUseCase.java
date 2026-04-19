package com.mailboom.api.application.whatsapp.port.in;

import com.mailboom.api.application.whatsapp.usecase.command.SendTemplateMessageCommand;
import com.mailboom.api.domain.model.common.valueobjects.Phone;
import java.util.List;
import java.util.UUID;

public interface SendTemplateMessageUseCase {
    void execute(SendTemplateMessageCommand command);
}
