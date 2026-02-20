package com.mailboom.api.application.user.usecase;

import com.mailboom.api.application.user.in.port.GetUserUseCase;
import com.mailboom.api.application.user.usecase.command.GetUserCommand;
import com.mailboom.api.domain.model.user.User;
import com.mailboom.api.domain.model.user.valueobjects.UserId;
import com.mailboom.api.domain.port.out.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@AllArgsConstructor
public class GetUserUseCaseImpl implements GetUserUseCase {
    private final UserRepository userRepository;

    @Override
    public User execute(GetUserCommand command) {
        UserId userId = new UserId(UUID.fromString(command.userId()));

        return userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
    }
}
