package com.mailboom.api.infrastructure.user.persistence.adapter;

import com.mailboom.api.domain.model.user.User;
import com.mailboom.api.domain.model.user.valueobjects.UserId;
import com.mailboom.api.domain.port.out.UserRepository;
import com.mailboom.api.infrastructure.common.exception.EmailNotFoundException;
import com.mailboom.api.infrastructure.user.persistence.jpa.entity.UserEntity;
import com.mailboom.api.infrastructure.user.persistence.jpa.mapper.UserEntityMapper;
import com.mailboom.api.infrastructure.user.persistence.jpa.repository.SpringDataUserRepository;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Primary
@AllArgsConstructor
public class UserRepositoryAdapter implements UserRepository {

    private final SpringDataUserRepository jpaRepository;
    private final UserEntityMapper userMapper;

    @Override
    public Optional<User> findById(UserId id) {
        return Optional.ofNullable(jpaRepository.findById(id.value())
                .map(userMapper::toDomain)
                .orElseThrow(() -> new EmailNotFoundException("User not found")));
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
    }


