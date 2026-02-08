package com.mailboom.api.infrastructure.campaign.controller;

import com.mailboom.api.application.campaign.port.in.CreateCampaignUseCase;
import com.mailboom.api.application.campaign.port.in.DeleteCampaignUseCase;
import com.mailboom.api.application.campaign.port.in.GetSentCampaignsFromUserUseCase;
import com.mailboom.api.application.campaign.usecase.command.CreateCampaignCommand;
import com.mailboom.api.application.campaign.usecase.command.DeleteCampaignCommand;
import com.mailboom.api.application.campaign.usecase.command.GetSentCampaignsFromUserCommand;
import com.mailboom.api.domain.model.campaign.Campaign;
import com.mailboom.api.infrastructure.campaign.dto.CampaignDataResponse;
import com.mailboom.api.infrastructure.campaign.dto.NewCampaignRequest;
import com.mailboom.api.infrastructure.campaign.dto.NewCampaignResponse;
import com.mailboom.api.infrastructure.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/campaigns")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
public class CampaignController {
    private final CreateCampaignUseCase createCampaignUseCase;
    private final GetSentCampaignsFromUserUseCase getSentCampaignsFromUserUseCase;
    private final DeleteCampaignUseCase deleteCampaignUseCase;

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

    @GetMapping("/user/{id}")
    @PreAuthorize("@userSecurity.isOwner(authentication, #id)")
    public ResponseEntity<List<CampaignDataResponse>> getSentCampaignsFromUser(@PathVariable UUID id) {
        GetSentCampaignsFromUserCommand getSentCampaignsFromUserCommand = new GetSentCampaignsFromUserCommand(id.toString());
        List<Campaign> sentCampaigns = getSentCampaignsFromUserUseCase.execute(getSentCampaignsFromUserCommand);
        return ResponseEntity.ok(sentCampaigns
                .stream()
                .map(
                        campaign -> new CampaignDataResponse(
                                campaign.getId().value().toString(),
                                campaign.getOwner().value().toString(),
                                campaign.getSubject().value(),
                                campaign.getHtmlContent().value(),
                                campaign.getSenderIdentity().value(),
                                campaign.getRecipientList().value().toString(),
                                campaign.getStatus().name(),
                                campaign.getCreatedAt().toString())).toList());
    }

    @DeleteMapping("/{id}/delete")
    @PreAuthorize("@userSecurity.isCampaignOwner(authentication, #id)")
    public ResponseEntity<Void> deleteCampaign(@PathVariable UUID id) {

        deleteCampaignUseCase.excecute(new DeleteCampaignCommand(id.toString()));
        return ResponseEntity.noContent().build();
    }
}
