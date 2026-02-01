package com.mailboom.api.infrastructure.persistence.jpa.mapper;

import com.mailboom.api.domain.model.User;
import com.mailboom.api.domain.model.valueobjects.*;
import com.mailboom.api.infrastructure.persistence.jpa.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserEntityMapper {

    public UserEntity toEntity(User user) {
        UserEntity entity = new UserEntity();
        entity.setId(user.getId().value());
        entity.setEmail(user.getEmail().email());
        entity.setPassword(user.getPassword().value());
        entity.setPlan(user.getPlan());
        entity.setEmailsSentThisMonth(user.getEmailsSentThisMonth().amountOfEmails());
        entity.setRole(user.getRole());
        return entity;
    }

    public User toDomain(UserEntity entity) {
        return User.create(
                new UserId(entity.getId()),
                new Email(entity.getEmail()),
                new Name(entity.getName()),
                new PasswordHash(entity.getPassword()),
                entity.getPlan(),
                new EmailCounter(entity.getEmailsSentThisMonth()),
                entity.getRole()
        );
    }
}
