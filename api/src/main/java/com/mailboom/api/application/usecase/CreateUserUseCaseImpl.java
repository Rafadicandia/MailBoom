package com.mailboom.api.application.usecase;

import com.mailboom.api.application.exception.UserWithEmailAlreadyExistsException;
import com.mailboom.api.application.port.in.CreateUserUseCase;
import com.mailboom.api.application.usecase.command.CreateUserCommand;
import com.mailboom.api.domain.model.User;
import com.mailboom.api.domain.port.out.UserRepository;
import com.mailboom.api.domain.model.valueobjects.*;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class CreateUserUseCaseImpl implements CreateUserUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public User execute(CreateUserCommand command) {
        if (userRepository.existsByEmail(command.email())) {
            throw new UserWithEmailAlreadyExistsException("User with email " + command.email() + " already exists");
        }

        UserId userId = UserId.generate();
        Email userEmail = Email.fromString(command.email());
        PasswordHash passwordHash = PasswordHash.fromString(passwordEncoder.encode(command.password()));
        Name name = Name.fromString(command.name());

        User newUser = User.create(userId, userEmail, name, passwordHash, EmailCounter.zero());
        return userRepository.save(newUser);
    }
}
