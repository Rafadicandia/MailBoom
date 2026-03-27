package com.mailboom.api.application.whatsapp.port.in;


import com.mailboom.api.domain.model.whatsapp.valueobjects.TemplateStatus;

import java.util.UUID;

public interface UpdateTemplateStatusUseCase {
    void updateStatus(UUID templateId, TemplateStatus status);
}
