package com.mailboom.api.infrastructure.whatsapp.dto;

import java.util.List;
import java.util.UUID;

public record SendTemplateMessageRequest(
        String toPhoneNumber, // Número de teléfono del destinatario en formato E.164 (ej: +5491122334455)
        String templateId,  // Nombre de la plantilla aprobada por Meta
        List<String> parameters, // Parámetros para las variables de la plantilla (ej: ["nombre", "producto"])
        UUID ownerId          // ID del propietario de la configuración de WhatsApp
) {
    public SendTemplateMessageRequest {
        if (toPhoneNumber == null || toPhoneNumber.isBlank()) {
            throw new IllegalArgumentException("Recipient phone number is required");
        }
        if (templateId == null || templateId.isBlank()) {
            throw new IllegalArgumentException("Template name is required");
        }
        if (ownerId == null) {
            throw new IllegalArgumentException("Owner ID is required");
        }
    }
}
