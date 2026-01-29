package com.mailboom.api.infrastructure.persistence.jpa.mapper;

import com.mailboom.api.domain.model.User;
import com.mailboom.api.domain.model.valueobjects.*;
import com.mailboom.api.infrastructure.persistence.jpa.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserEntity toEntity(User user) {
        UserEntity entity = new UserEntity();
        entity.setId(user.getId().value());
        entity.setEmail(user.getEmail().email());
        entity.setPassword(user.getPassword().value());
        entity.setPlan(user.getPlan());
        entity.setEmailsSentThisMonth(user.getEmailsSentThisMonth().amountOfEmails());
        return entity;
    }

    public User toDomain(UserEntity entity) {
        return User.create(
                new UserId(entity.getId()),
                new Email(entity.getEmail()),
                new PasswordHash(entity.getPassword()),
                entity.getPlan(),
                new EmailCounter(entity.getEmailsSentThisMonth())
        );
    }
}
