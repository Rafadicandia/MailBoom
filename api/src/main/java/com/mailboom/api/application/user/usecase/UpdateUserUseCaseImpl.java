package com.mailboom.api.application.user.usecase;

import com.mailboom.api.application.user.in.port.UpdateUserUseCase;
import com.mailboom.api.application.user.usecase.command.UpdateUserCommand;
import com.mailboom.api.domain.model.common.valueobjects.Email;
import com.mailboom.api.domain.model.common.valueobjects.Name;
import com.mailboom.api.domain.model.user.User;
import com.mailboom.api.domain.model.user.valueobjects.PasswordHash;
import com.mailboom.api.domain.model.user.valueobjects.UserId;
import com.mailboom.api.domain.port.out.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UpdateUserUseCaseImpl implements UpdateUserUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User execute(UpdateUserCommand command) {
        UserId userId = new UserId(UUID.fromString(command.userId()));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Email email = user.getEmail();
        if (command.email() != null && !command.email().isBlank()) {
            email = new Email(command.email());
        }

        Name name = user.getName();
        if (command.name() != null && !command.name().isBlank()) {
            name = new Name(command.name());
        }

        PasswordHash password = user.getPassword();
        if (command.password() != null && !command.password().isBlank()) {
            password = new PasswordHash(passwordEncoder.encode(command.password()));
        }

        User updatedUser = User.create(
                user.getId(),
                email,
                name,
                password,
                user.getPlan(),
                user.getEmailCounter(),
                user.getRole(),
                user.getContactLists()
        );

        return userRepository.save(updatedUser);
    }
}
