package com.mailboom.api.infrastructure.security;

import com.mailboom.api.application.exception.UserWithEmailAlreadyExistsException;
import com.mailboom.api.application.port.in.CreateUserUseCase;
import com.mailboom.api.application.usecase.command.CreateUserCommand;
import com.mailboom.api.domain.model.User;
import com.mailboom.api.domain.model.valueobjects.*;
import com.mailboom.api.domain.repository.UserRepository;
import com.mailboom.api.infrastructure.dto.LoginRequest;
import com.mailboom.api.infrastructure.dto.TokenResponse;
import com.mailboom.api.infrastructure.persistence.jpa.entity.TokenEntity;
import com.mailboom.api.infrastructure.persistence.jpa.entity.UserEntity;
import com.mailboom.api.infrastructure.persistence.jpa.mapper.UserEntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final CreateUserUseCase createUserUseCase;
    private final UserEntityMapper mapper;



    public TokenResponse register(CreateUserCommand command) {

        User newUser = createUserUseCase.execute(command);
        UserEntity savedUser = mapper.toEntity(newUser);
        var jwtToken = jwtService.generateToken(savedUser);
        var refreshToken = jwtService.generateRefreshToken(savedUser);
        saveUserToken(savedUser, jwtToken);

    }

    public TokenResponse login(LoginRequest request){
        return  null;
    }

    private void saveUserToken(UserEntity user, String jwtToken){
        var token = TokenEntity.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenEntity.TokenType.BEARER)
                .revoked(false)
                .expired(false)
                .build();
        tokenRepository.save(token);
    }

    public TokenResponse refreshToken(final String authHeader){
        return null;
    }
}
