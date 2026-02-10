package com.mailboom.api.infrastructure.campaign.controller;

import com.mailboom.api.application.campaign.port.in.*;
import com.mailboom.api.application.campaign.usecase.command.*;
import com.mailboom.api.domain.model.campaign.Campaign;
import com.mailboom.api.infrastructure.campaign.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/campaigns")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
public class CampaignController {
    private final CreateCampaignUseCase createCampaignUseCase;
    private final GetSentCampaignsFromUserUseCase getSentCampaignsFromUserUseCase;
    private final DeleteCampaignUseCase deleteCampaignUseCase;
    private final UpdateCampaignUseCase updateCampaignUseCase;
    private final GetCampaignUseCase getCampaignUseCase;
    private final SendCampaignUseCase sendCampaignUseCase;


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
                                campaign.getSenderIdentity().clientName(),
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

    @PutMapping("/{id}/update")
    @PreAuthorize("@userSecurity.isCampaignOwner(authentication, #id)")
    public ResponseEntity<CampaignDataResponse> updateCampaign(
            @PathVariable UUID id,
            @RequestBody UpdateCampaignRequest request) {
        UpdateCampaignCommand updateCampaignCommand = new UpdateCampaignCommand(
                id.toString(),
                request.subject(),
                request.htmlContent(),
                request.sender(),
                request.recipientListId()
        );

        Campaign updatedCampaign = updateCampaignUseCase.execute(updateCampaignCommand);
        return ResponseEntity.ok(
                new CampaignDataResponse(
                        updatedCampaign.getId().value().toString(),
                        updatedCampaign.getOwner().value().toString(),
                        updatedCampaign.getSubject().value(),
                        updatedCampaign.getHtmlContent().value(),
                        updatedCampaign.getSenderIdentity().clientName(),
                        updatedCampaign.getRecipientList().value().toString(),
                        updatedCampaign.getStatus().name(),
                        updatedCampaign.getCreatedAt().toString()
                ));
    }

    @GetMapping("/{id}")
    @PreAuthorize("@userSecurity.isCampaignOwner(authentication, #id)")
    public ResponseEntity<CampaignDataResponse> getCampaign(@PathVariable UUID id) {
        GetCampaignCommand getCampaignCommand = new GetCampaignCommand(id.toString());
        Campaign campaign = getCampaignUseCase.excecute(getCampaignCommand);
        return ResponseEntity.ok(
                new CampaignDataResponse(
                        campaign.getId().value().toString(),
                        campaign.getOwner().value().toString(),
                        campaign.getSubject().value(),
                        campaign.getHtmlContent().value(),
                        campaign.getSenderIdentity().clientName(),
                        campaign.getRecipientList().value().toString(),
                        campaign.getStatus().name(),
                        campaign.getCreatedAt().toString()
                ));
    }

    @PostMapping ("/{id}/send")
    @PreAuthorize("@userSecurity.isCampaignOwner(authentication, #id)")
    public ResponseEntity<Void> sendCampaign(@PathVariable UUID id, @RequestBody SendCampaignRequest request) {
        SendCampaignCommand sendCampaignCommand = new SendCampaignCommand(id.toString(), request.ownerId());
        sendCampaignUseCase.execute(sendCampaignCommand);
        return ResponseEntity.noContent().build();
    }


}
