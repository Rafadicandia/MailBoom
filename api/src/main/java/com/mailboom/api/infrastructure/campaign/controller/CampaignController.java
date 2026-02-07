package com.mailboom.api.infrastructure.campaign.controller;

import com.mailboom.api.application.campaign.port.in.CreateCampaignUseCase;
import com.mailboom.api.application.campaign.usecase.command.CreateCampaignCommand;
import com.mailboom.api.domain.model.campaign.Campaign;
import com.mailboom.api.infrastructure.campaign.dto.NewCampaignRequest;
import com.mailboom.api.infrastructure.campaign.dto.NewCampaignResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/campaigns")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
public class CampaignController {
    private final CreateCampaignUseCase createCampaignUseCase;

    @PostMapping("/new")
    @PreAuthorize("@userSecurity.isOwner(authentication, #request.ownerId())")
    public ResponseEntity<NewCampaignResponse> createCampaign(@RequestBody NewCampaignRequest request) {
        CreateCampaignCommand createCampaignCommand = new CreateCampaignCommand(
                request.ownerId(),
                request.subject(),
                request.htmlContent(),
                request.sender(),
                request.recipientListId()
        );
        Campaign newCampaign = createCampaignUseCase.execute(createCampaignCommand);
        return ResponseEntity.ok(new NewCampaignResponse(
                newCampaign.getId().value().toString(),
                newCampaign.getOwner().value().toString(),
                newCampaign.getSubject().value(),
                newCampaign.getHtmlContent().value(),
                newCampaign.getSenderIdentity().clientName(),
                newCampaign.getRecipientList().value().toString(),
                newCampaign.getStatus().name(),
                newCampaign.getCreatedAt().toString()
        ));


    }
}
