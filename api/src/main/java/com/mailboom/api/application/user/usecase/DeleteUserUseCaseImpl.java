package com.mailboom.api.application.user.usecase;

import com.mailboom.api.application.user.in.port.DeleteUserUseCase;
import com.mailboom.api.application.user.usecase.command.DeleteUserCommand;
import com.mailboom.api.domain.model.user.valueobjects.UserId;
import com.mailboom.api.domain.port.out.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DeleteUserUseCaseImpl implements DeleteUserUseCase {

    private final UserRepository userRepository;

    @Override
    public void execute(DeleteUserCommand command) {
        UserId userId = new UserId(UUID.fromString(command.userId()));
        userRepository.delete(userId);
    }
}
