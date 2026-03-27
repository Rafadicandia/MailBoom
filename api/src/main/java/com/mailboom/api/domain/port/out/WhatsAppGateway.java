package com.mailboom.api.domain.port.out;

import com.mailboom.api.domain.model.whatsapp.Template;

public interface WhatsAppGateway {
    void sendTemplateForReview(Template template);
}
