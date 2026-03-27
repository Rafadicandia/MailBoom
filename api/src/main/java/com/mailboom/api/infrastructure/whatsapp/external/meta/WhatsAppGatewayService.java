package com.mailboom.api.infrastructure.whatsapp.external.meta;


import com.mailboom.api.domain.model.whatsapp.Template;
import com.mailboom.api.domain.port.out.WhatsAppGateway;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Component
@AllArgsConstructor
public class WhatsAppGatewayService implements WhatsAppGateway {

    private final WebClient webClient;
    private final MetaConfig config;


    @Override
    public void sendTemplateForReview(Template template) {
        System.out.println("Sending template to Meta for review: " + template.getName());
        // Mapeo de nuestro Dominio al JSON que pide Meta (el de tu curl)
        Map<String, Object> body = Map.of(
                "name", template.getName(),
                "category", template.getCategory(),
                "language", template.getLanguage(),
                "components", List.of(Map.of(
                        "type", "BODY",
                        "text", template.getBodyText()
                ))
        );

        return webClient.post()
                .uri("/{wabaId}/message_templates", config.getWabaId())
                .header("Authorization", "Bearer " + config.getAccessToken())
                .bodyValue(body)
                .retrieve()
                .bodyToMono(MetaResponse.class)
                .map(MetaResponse::getId) // Retorna el ID de la plantilla en Meta
                .block(); // En el Use Case podemos manejarlo de forma síncrona o reactiva
    }

}



