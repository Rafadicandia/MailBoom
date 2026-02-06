package com.mailboom.api.infrastructure.user.persistence.jpa.mapper;

import com.mailboom.api.domain.model.common.valueobjects.Email;
import com.mailboom.api.domain.model.common.valueobjects.Name;
import com.mailboom.api.domain.model.user.User;
import com.mailboom.api.domain.model.user.valueobjects.EmailCounter;
import com.mailboom.api.domain.model.user.valueobjects.PasswordHash;
import com.mailboom.api.domain.model.user.valueobjects.UserId;
import com.mailboom.api.infrastructure.user.persistence.jpa.entity.UserEntity;
import org.springframework.stereotype.Component;

import java.util.HashSet;

@Component
public class UserEntityMapper {

    public UserEntity toEntity(User user) {
        UserEntity entity = new UserEntity();
        entity.setId(user.getId().value());
        entity.setEmail(user.getEmail().email());
        entity.setName(user.getName().toString());
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
                entity.getRole(),
                new HashSet<>()
        );
    }
}
