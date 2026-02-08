package com.mailboom.api.application.campaign.usecase;

import com.mailboom.api.application.campaign.port.in.GetSentCampaignsFromUserUseCase;
import com.mailboom.api.application.campaign.usecase.command.GetSentCampaignsFromUserCommand;
import com.mailboom.api.application.common.exception.UserIdNotFoundException;
import com.mailboom.api.domain.model.campaign.Campaign;
import com.mailboom.api.domain.model.user.valueobjects.UserId;
import com.mailboom.api.domain.port.out.CampaignRepository;
import com.mailboom.api.domain.port.out.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class GetSentCampaignsFromUserUSeCaseImpl implements GetSentCampaignsFromUserUseCase {
    private final CampaignRepository campaignRepository;
    private final UserRepository userRepository;

    @Override
    public List<Campaign> execute(GetSentCampaignsFromUserCommand command) {
        UserId userId = new UserId(UUID.fromString(command.Userid()));
        if (userRepository.findById(userId) == null) {
            throw new UserIdNotFoundException("User not found: " + command.Userid());
        }

        return campaignRepository.findAllByOwnerId(userId);
    }
}
