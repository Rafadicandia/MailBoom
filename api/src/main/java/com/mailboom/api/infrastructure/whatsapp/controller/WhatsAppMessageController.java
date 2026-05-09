package com.mailboom.api.infrastructure.whatsapp.controller;

import com.mailboom.api.application.whatsapp.port.in.SendTemplateMessageUseCase;
import com.mailboom.api.application.whatsapp.port.in.SendTemplateToListUseCase;
import com.mailboom.api.application.whatsapp.usecase.command.SendTemplateMessageCommand;
import com.mailboom.api.application.whatsapp.usecase.command.SendTemplateToListCommand;
import com.mailboom.api.domain.model.contact.valueobjects.ContactListId;
import com.mailboom.api.infrastructure.whatsapp.dto.SendTemplateMessageRequest;
import com.mailboom.api.infrastructure.whatsapp.dto.SendTemplateToListRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/whatsapp/messages")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
@Tag(name = "Whatsapp Messages", description = "Operations related to sending WhatsApp messages")
public class WhatsAppMessageController {

    private final SendTemplateMessageUseCase sendTemplateMessageUseCase;
    private final SendTemplateToListUseCase sendTemplateToListUseCase; // Inyectar el nuevo caso de uso

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

    @PostMapping("/send-list")
    @Operation(summary = "Send a template message to a list of contacts")
    public ResponseEntity<Void> sendTemplateToList(@RequestBody SendTemplateToListRequest request) {
        sendTemplateToListUseCase.execute(
                new SendTemplateToListCommand(
                        request.templateName(),
                        new ContactListId(request.to())
                )
        );
        return ResponseEntity.ok().build();
    }
}
