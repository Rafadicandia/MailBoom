package com.mailboom.api.infrastructure.whatsapp.controller;

import com.mailboom.api.application.whatsapp.port.in.SendTemplateMessageUseCase;
import com.mailboom.api.application.whatsapp.usecase.command.SendTemplateMessageCommand;
import com.mailboom.api.domain.model.common.valueobjects.Phone;
import com.mailboom.api.infrastructure.whatsapp.dto.SendTemplateMessageRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/whatsapp/messages")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
@Tag(name = "Whatsapp Messages", description = "Operations related to sending WhatsApp messages")
public class WhatsAppMessageController {

    private final SendTemplateMessageUseCase sendTemplateMessageUseCase;

    @PostMapping("/send-template")
    @Operation(summary = "Send a template-based message")
    public ResponseEntity<Void> sendTemplate(@RequestBody SendTemplateMessageRequest request) {
        sendTemplateMessageUseCase.execute(
                new SendTemplateMessageCommand(
                        UUID.fromString(request.templateId()),
                        request.toPhoneNumber(),
                        request.parameters()
                )
        );
        return ResponseEntity.ok().build();
    }

}
