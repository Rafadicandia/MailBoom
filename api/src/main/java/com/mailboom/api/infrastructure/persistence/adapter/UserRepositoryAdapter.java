package com.mailboom.api.infrastructure.persistence.adapter;

import com.mailboom.api.domain.model.User;
import com.mailboom.api.domain.model.valueobjects.UserId;
import com.mailboom.api.domain.repository.UserRepository;
import com.mailboom.api.infrastructure.exception.EmailNotFoundException;
import com.mailboom.api.infrastructure.persistence.jpa.entity.UserEntity;
import com.mailboom.api.infrastructure.persistence.jpa.mapper.UserEntityMapper;
import com.mailboom.api.infrastructure.persistence.jpa.repository.SpringDataUserRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

@Repository
@Primary
public class UserRepositoryAdapter implements UserRepository {

    private final SpringDataUserRepository jpaRepository;
    private final UserEntityMapper userMapper;

    public UserRepositoryAdapter(SpringDataUserRepository jpaRepository, UserEntityMapper userMapper) {
        this.jpaRepository = jpaRepository;
        this.userMapper = userMapper;
    }

    @Override
    public User findById(UserId id) {
        return jpaRepository.findById(id.value())
                .map(userMapper::toDomain)
                .orElse(null);
    }

    @Override
    public User save(User user) {
        UserEntity entity = userMapper.toEntity(user);
        UserEntity savedEntity = jpaRepository.save(entity);
        return userMapper.toDomain(savedEntity);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaRepository.existsByEmail(email);
    }

    @Override
    public User findByEmail(String email) {
        if (!jpaRepository.existsByEmail(email)) {
            throw new EmailNotFoundException("Email not found");
        }
        return jpaRepository.findByEmail(email)
                .map(userMapper::toDomain)
                .orElse(null);
    }

    @Override
    public void delete(UserId id) {
        jpaRepository.deleteById(id.value());
    }

    @Override
    public void incrementEmailsSent(UserId userId, int quantity) {
        jpaRepository.findById(userId.value()).ifPresent(entity -> {
            entity.setEmailsSentThisMonth(entity.getEmailsSentThisMonth() + quantity);
            jpaRepository.save(entity);
        });
    }

    @Override
    public boolean canUserSendEmails(UserId userId, int quantity) {
        return jpaRepository.findById(userId.value())
                .map(userMapper::toDomain)
                .map(user -> user.canSendMoreEmails(quantity))
                .orElse(false);
    }

    @Override
    public void resetMonthlyEmails(UserId userId) {
        jpaRepository.findById(userId.value()).ifPresent(entity -> {
            entity.setEmailsSentThisMonth(0);
            jpaRepository.save(entity);
        });
    }
}
