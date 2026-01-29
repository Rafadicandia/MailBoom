package com.mailboom.api.application.usecase;

import com.mailboom.api.domain.model.User;
import com.mailboom.api.domain.model.valueobjects.*;
import com.mailboom.api.infrastructure.persistence.adapter.UserRepositoryAdapter;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
@AllArgsConstructor
public class CreateNewUserUseCase {
    private final UserRepositoryAdapter userRepository;


    public User execute(String email, String password, PlanType plan) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("User with email " + email + " already exists");
        }

        UserId userId = UserId.generate();
        Email userEmail = Email.fromString(email);
        PasswordHash passwordHash = PasswordHash.fromString(password);

        User newUser = User.create(userId, userEmail, passwordHash, plan, EmailCounter.zero());
        return userRepository.save(newUser);
    }
}
