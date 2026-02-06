package com.mailboom.api.infrastructure.user.persistence.jpa.repository;

import com.mailboom.api.infrastructure.user.persistence.jpa.entity.TokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface TokenRepository extends JpaRepository<TokenEntity, Long> {

    @Query("""
            select t from tokens t inner join UserEntity u on t.user.id = u.id
            where u.id = :userId and (t.expired = false or t.revoked = false)
            """)
    List<TokenEntity> findAllValidTokensByUserId(UUID userId);

    Optional<TokenEntity> findByToken(String token);
}
