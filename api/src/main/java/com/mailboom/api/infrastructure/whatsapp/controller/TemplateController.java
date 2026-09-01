package com.mailboom.api.infrastructure.whatsapp.controller;


import com.mailboom.api.application.whatsapp.port.in.CreateTemplateUseCase;
import com.mailboom.api.application.whatsapp.port.in.DeleteTemplateUseCase;
import com.mailboom.api.application.whatsapp.port.in.GetAllTemplatesFromUserUseCase;
import com.mailboom.api.application.whatsapp.port.in.GetTemplateByNameUseCase;
import com.mailboom.api.application.whatsapp.usecase.command.CreateTemplateCommand;
import com.mailboom.api.application.whatsapp.usecase.command.DeleteTemplateCommand;
import com.mailboom.api.application.whatsapp.usecase.command.GetAllTemplatesFromUserCommand;
import com.mailboom.api.application.whatsapp.usecase.command.GetTemplateByNameCommand;
import com.mailboom.api.domain.model.whatsapp.Template;
import com.mailboom.api.infrastructure.whatsapp.dto.CreateTemplateRequest;
import com.mailboom.api.infrastructure.whatsapp.dto.CreateTemplateResponse;
import com.mailboom.api.infrastructure.whatsapp.dto.TemplateResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/whatsapp")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
@Tag(name = "Whatsapp Templates", description = "Operations related to Whatsapp templates")
public class TemplateController {

    private final CreateTemplateUseCase createTemplateUseCase;
    private final GetTemplateByNameUseCase getTemplateUseCase;
    private final GetAllTemplatesFromUserUseCase getAllTemplatesFromUserUseCase;
    private final DeleteTemplateUseCase deleteTemplateUseCase;

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
        return ResponseEntity.ok(new CreateTemplateResponse(
                        createdTemplate.getId().toString(),
                        createdTemplate.getName(),
                        createdTemplate.getCategory().name(),
                        createdTemplate.getLanguage().getCode(),
                        createdTemplate.getComponents(),
                        createdTemplate.getStatus().name(),
                        createdTemplate.getOwnerId().toString()
                )
        );
    }

    @GetMapping("/templates/{name}")
    @Operation(summary = "Get a template by name")
    public ResponseEntity<TemplateResponse> getTemplate(@PathVariable String name) {
        Template template = getTemplateUseCase.execute(new GetTemplateByNameCommand(name));

        return ResponseEntity.ok(new TemplateResponse(
                template.getId().toString(),
                template.getName(),
                template.getCategory().name(),
                template.getLanguage().getCode(),
                template.getComponents(),
                template.getStatus().name(),
                template.getOwnerId().toString()
        ))
                ;

    }

    @GetMapping("/templates")
    @Operation(summary = "View all Templates from user")
    @PreAuthorize("@userSecurity.isOwner(authentication, #userId)")
    public ResponseEntity<List<TemplateResponse>> getAllTemplatesFromUser(@RequestParam UUID userId) {

        GetAllTemplatesFromUserCommand command = new GetAllTemplatesFromUserCommand(userId.toString());
        List<Template> templates = getAllTemplatesFromUserUseCase.execute(command);

        return ResponseEntity.ok(
                templates.stream().map(
                        template -> new TemplateResponse(
                                template.getId().toString(),
                                template.getName(),
                                template.getCategory().toString(),
                                template.getLanguage().toString(),
                                template.getComponents(),
                                template.getStatus().toString(),
                                template.getOwnerId().toString()
                        )
                ).toList()

        );
    }

    @DeleteMapping("/{id}/delete")
    @Operation(summary = "Delete template by id")
    @PreAuthorize("@userSecurity.isOwner(authentication, #userId)")
    public ResponseEntity<Void> deleteTemplate(@PathVariable UUID id) {

        deleteTemplateUseCase.delete(new DeleteTemplateCommand(id));
        return ResponseEntity.noContent().build();
    }

}

