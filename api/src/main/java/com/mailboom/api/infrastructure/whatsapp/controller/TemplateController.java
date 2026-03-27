package com.mailboom.api.infrastructure.whatsapp.controller;

import com.mailboom.api.application.whatsapp.port.in.CreateTemplateUseCase;
import com.mailboom.api.application.whatsapp.usecase.command.CreateTemplateCommand;
import com.mailboom.api.domain.model.whatsapp.Template;
import com.mailboom.api.infrastructure.whatsapp.dto.CreateTemplateRequest;
import com.mailboom.api.infrastructure.whatsapp.dto.CreateTemplateResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/whatsapp")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
@Tag(name = "Whatsapp Templates", description = "Operations related to Whatsapp templates")
public class TemplateController {

    private final CreateTemplateUseCase createTemplateUseCase;


    @PostMapping("/templates/new")
    @PreAuthorize("@userSecurity.isOwner(authentication, #request.ownerId())")
    @Operation(summary = "Create a new template")
    public ResponseEntity<CreateTemplateResponse> createTemplate(@RequestBody CreateTemplateRequest request) {
        CreateTemplateCommand command = new CreateTemplateCommand(
                request.name(),
                request.category(),
                request.parameterFormat(),
                request.components(),
                request.language(),
                request.ownerId()

        );
        Template createdTemplate = createTemplateUseCase.execute(command);
        CreateTemplateResponse response = new CreateTemplateResponse(
                createdTemplate.getId().toString(),
                createdTemplate.getName(),
                createdTemplate.getCategory().toString(),
                createdTemplate.getLanguage().toString(),
                createdTemplate.getComponents(),
                createdTemplate.getStatus().toString(),
                createdTemplate.getOwnerId().toString()
        );
        return ResponseEntity.ok(response);
    }

}
