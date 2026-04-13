package com.mailboom.api.infrastructure.whatsapp.external.meta;

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
}
