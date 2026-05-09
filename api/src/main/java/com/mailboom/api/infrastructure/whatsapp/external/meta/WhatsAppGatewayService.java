package com.mailboom.api.infrastructure.whatsapp.external.meta;

import com.mailboom.api.domain.model.common.valueobjects.Phone;
import com.mailboom.api.domain.model.whatsapp.ClientConfig;
import com.mailboom.api.domain.model.whatsapp.Template;
import com.mailboom.api.domain.port.out.WhatsAppGateway;
import com.mailboom.api.infrastructure.whatsapp.dto.MetaTemplateResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@AllArgsConstructor
public class WhatsAppGatewayService implements WhatsAppGateway {

    private final WebClient webClient;

    @Override
    public void sendTemplateForReview(Template template, ClientConfig config) {
        log.info("Sending template '{}' to Meta for review for WABA ID: {}", template.getName(), config.getWabaId());

        Map<String, Object> body = Map.of(
                "name", template.getName(),
                "category", template.getCategory().name(),
                "language", template.getLanguage().getCode(),
                "components", template.getComponents().stream()
                        .map(comp -> Map.of(
                                "type", comp.type().name(),
                                "text", comp.text()
                        ))
                        .collect(Collectors.toList())
        );

        try {
            MetaTemplateResponse response = webClient.post()
                    .uri("/{wabaId}/message_templates", config.getWabaId())
                    .header("Authorization", "Bearer " + config.getAccessToken().value())
                    .bodyValue(body)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, clientResponse ->
                            clientResponse.bodyToMono(String.class)
                                    .flatMap(errorBody -> {
                                        log.error("Error from Meta API: Status Code: {}, Body: {}",
                                                clientResponse.statusCode(), errorBody);
                                        return Mono.error(new RuntimeException("Meta API Error: " + errorBody));
                                    })
                    )
                    .bodyToMono(MetaTemplateResponse.class)
                    .block();

            if (response != null) {
                log.info("Template successfully sent to Meta. Meta Template ID: {}, Status: {}",
                        response.id(), response.status());
            }
        } catch (Exception e) {
            log.error("Failed to send template to Meta: {}", e.getMessage());
            throw new RuntimeException("Error communicating with WhatsApp Meta API", e);
        }
    }

    @Override
    public void sendTemplateMessage(Phone to, Template template, List<String> parameters, ClientConfig config) {
        log.info("Sending WhatsApp template message to {} using template '{}'", to.phone(), template.getName());

        // Construcción del cuerpo del mensaje para Meta API
        Map<String, Object> templateData = Map.of(
                "name", template.getName(),
                "language", Map.of("code", template.getLanguage().getCode())
        );

        // Si hay parámetros, los añadimos al componente "body"
        if (parameters != null && !parameters.isEmpty()) {
            List<Map<String, String>> parameterList = parameters.stream()
                    .map(param -> Map.of("type", "text", "text", param))
                    .toList();

            templateData = Map.of(
                    "name", template.getName(),
                    "language", Map.of("code", template.getLanguage().getCode()),
                    "components", List.of(
                            Map.of(
                                    "type", "body",
                                    "parameters", parameterList
                            )
                    )
            );
        }

        Map<String, Object> body = Map.of(
                "messaging_product", "whatsapp",
                "to", to.phone(),
                "type", "template",
                "template", templateData
        );

        try {
            webClient.post()
                    .uri("/{phoneNumberId}/messages", config.getPhoneNumberId())
                    .header("Authorization", "Bearer " + config.getAccessToken().value())
                    .bodyValue(body)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, clientResponse ->
                            clientResponse.bodyToMono(String.class)
                                    .flatMap(errorBody -> {
                                        log.error("Error from Meta API (Send): Status Code: {}, Body: {}",
                                                clientResponse.statusCode(), errorBody);
                                        return Mono.error(new RuntimeException("Meta API Error: " + errorBody));
                                    })
                    )
                    .toBodilessEntity()
                    .block();

            log.info("WhatsApp message successfully sent to {}", to.phone());
        } catch (Exception e) {
            log.error("Failed to send WhatsApp message: {}", e.getMessage());
            throw new RuntimeException("Error sending WhatsApp message via Meta API", e);
        }
    }
}
