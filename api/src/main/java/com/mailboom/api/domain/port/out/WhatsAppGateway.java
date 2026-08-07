package com.mailboom.api.domain.port.out;

import com.mailboom.api.domain.model.whatsapp.ClientConfig;
import com.mailboom.api.domain.model.whatsapp.Template;
import com.mailboom.api.domain.model.common.valueobjects.Phone;
import com.mailboom.api.infrastructure.whatsapp.dto.MetaTemplateResponse;

import java.util.List;

public interface WhatsAppGateway {
    MetaTemplateResponse sendTemplateForReview(Template template, ClientConfig config);

    void sendTemplateMessage(Phone to, Template template, List<String> parameters, ClientConfig config);
}
