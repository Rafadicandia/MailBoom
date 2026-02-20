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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Primary
@AllArgsConstructor
public class UserRepositoryAdapter implements UserRepository {

    private final SpringDataUserRepository jpaRepository;
    private final UserEntityMapper userMapper;

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findById(UserId id) {
        return jpaRepository.findById(id.value())
                .map(userMapper::toDomain);
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
    @Transactional(readOnly = true)
    public User findByEmail(String email) {
        return jpaRepository.findByEmail(email)
                .map(userMapper::toDomain)
                .orElseThrow(() -> new EmailNotFoundException("Email not found"));
    }

    @Override
    public void delete(UserId id) {
        jpaRepository.deleteById(id.value());
    }


    @Override
    public List<User> findAllUsers() {
        return jpaRepository.findAll().stream().map(userMapper::toDomain).toList();
    }

    @Override
    public long count() {
        return jpaRepository.count();
    }
}
