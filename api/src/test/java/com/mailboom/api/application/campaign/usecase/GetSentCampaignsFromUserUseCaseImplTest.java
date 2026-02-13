package com.mailboom.api.application.campaign.usecase;

import com.mailboom.api.application.campaign.usecase.command.GetSentCampaignsFromUserCommand;
import com.mailboom.api.application.common.exception.UserIdNotFoundException;
import com.mailboom.api.domain.model.campaign.Campaign;
import com.mailboom.api.domain.model.user.User;
import com.mailboom.api.domain.model.user.valueobjects.UserId;
import com.mailboom.api.domain.port.out.CampaignRepository;
import com.mailboom.api.domain.port.out.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetSentCampaignsFromUserUseCaseImplTest {

    @Mock
    private CampaignRepository campaignRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private GetSentCampaignsFromUserUSeCaseImpl getSentCampaignsFromUserUseCase;

    private GetSentCampaignsFromUserCommand command;
    private UserId userId;

    @BeforeEach
    void setUp() {
        userId = new UserId(UUID.randomUUID());
        command = new GetSentCampaignsFromUserCommand(userId.value().toString());
    }

    @Test
    void shouldGetSentCampaignsSuccessfully() {
        // Given
        when(userRepository.findById(userId)).thenReturn(Optional.of(mock(User.class)));
        when(campaignRepository.findAllByOwnerId(userId)).thenReturn(Collections.emptyList());

        // When
        List<Campaign> result = getSentCampaignsFromUserUseCase.execute(command);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        // Given
        when(userRepository.findById(userId)).thenReturn(null);

        // When & Then
        assertThrows(UserIdNotFoundException.class, () -> getSentCampaignsFromUserUseCase.execute(command));
    }
}
