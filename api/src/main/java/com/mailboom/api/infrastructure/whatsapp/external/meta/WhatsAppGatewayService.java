package com.mailboom.api.infrastructure.whatsapp.external.meta;

import com.mailboom.api.domain.model.common.valueobjects.Phone;
import com.mailboom.api.domain.model.whatsapp.ClientConfig;
import com.mailboom.api.domain.model.whatsapp.Template;
import com.mailboom.api.domain.model.whatsapp.valueobjects.ComponentType;
import com.mailboom.api.domain.port.out.WhatsAppGateway;
import com.mailboom.api.infrastructure.whatsapp.dto.MetaTemplateResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@AllArgsConstructor
public class WhatsAppGatewayService implements WhatsAppGateway {

    private final WebClient webClient;

    @Override
    public MetaTemplateResponse sendTemplateForReview(Template template, ClientConfig config) {
        List<Map<String, Object>> componentsPayload = template.getComponents().stream().map(comp -> {
            Map<String, Object> m = new HashMap<>();
            m.put("type", comp.type().name());

            // Para componentes BODY, HEADER y FOOTER se incluye el texto
            if (comp.type() != ComponentType.BUTTONS && comp.text() != null) {
                m.put("text", comp.text());
            }

            // example viene como {"body_text": [["Aldrina"]]} ya en el formato de Meta
            if (comp.example() != null) {
                m.put("example", comp.example());
            }

            if (comp.type() == ComponentType.BUTTONS) {
                if (comp.buttons() == null || comp.buttons().isEmpty()) {
                    throw new IllegalArgumentException("BUTTONS component must have at least one button defined");
                }
                List<Map<String, Object>> btns = comp.buttons().stream().map(b -> {
                    Map<String, Object> bm = new HashMap<>();
                    bm.put("type", b.type());
                    bm.put("text", b.text());
                    if (b.url() != null) {
                        bm.put("url", b.url());
                    }
                    return bm;
                }).collect(Collectors.toList());
                m.put("buttons", btns);
            } else if (comp.buttons() != null && !comp.buttons().isEmpty()) {
                List<Map<String, Object>> btns = comp.buttons().stream().map(b -> {
                    Map<String, Object> bm = new HashMap<>();
                    bm.put("type", b.type());
                    bm.put("text", b.text());
                    if (b.url() != null) {
                        bm.put("url", b.url());
                    }
                    return bm;
                }).collect(Collectors.toList());
                m.put("buttons", btns);
            }
            return m;
        }).collect(Collectors.toList());

        Map<String, Object> body = Map.of(
                "name", template.getName(),
                "category", template.getCategory().name(),
                "language", template.getLanguage().getCode(),
                "components", componentsPayload
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
            return response;
        } catch (Exception e) {
            log.error("Failed to send template to Meta: {}", e.getMessage());
            throw new RuntimeException("Error communicating with WhatsApp Meta API", e);
        }
    }

    @Override
    public void sendTemplateMessage(Phone to, Template template, List<String> parameters, ClientConfig config) {
        // ... tu metodo de envio igual que antes, no tocar
        log.info("Sending WhatsApp template message to {} using template '{}'", to.phone(), template.getName());
        Map<String, Object> templateData = Map.of(
                "name", template.getName(),
                "language", Map.of("code", template.getLanguage().getCode())
        );
        if (parameters != null && !parameters.isEmpty()) {
            List<Map<String, String>> parameterList = parameters.stream()
                    .map(param -> Map.of("type", "text", "text", param))
                    .toList();
            templateData = Map.of(
                    "name", template.getName(),
                    "language", Map.of("code", template.getLanguage().getCode()),
                    "components", List.of(
                            Map.of("type", "body", "parameters", parameterList)
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
                                        log.error("Error from Meta API (Send): Status Code: {}, Body: {}", clientResponse.statusCode(), errorBody);
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