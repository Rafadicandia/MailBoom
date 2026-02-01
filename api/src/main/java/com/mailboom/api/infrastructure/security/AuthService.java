package com.mailboom.api.infrastructure.security;


import com.mailboom.api.application.port.in.CreateUserUseCase;
import com.mailboom.api.application.usecase.command.CreateUserCommand;
import com.mailboom.api.domain.model.User;
import com.mailboom.api.infrastructure.dto.LoginRequest;
import com.mailboom.api.infrastructure.dto.TokenResponse;
import com.mailboom.api.infrastructure.persistence.jpa.entity.TokenEntity;
import com.mailboom.api.infrastructure.persistence.jpa.entity.UserEntity;
import com.mailboom.api.infrastructure.persistence.jpa.mapper.UserEntityMapper;
import com.mailboom.api.infrastructure.persistence.jpa.repository.SpringDataUserRepository;
import com.mailboom.api.infrastructure.persistence.jpa.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final TokenRepository tokenRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final CreateUserUseCase createUserUseCase;
    private final UserEntityMapper mapper;
    private final SpringDataUserRepository userRepository;


    public TokenResponse register(CreateUserCommand command) {

        User newUser = createUserUseCase.execute(command);
        UserEntity savedUser = mapper.toEntity(newUser);
        var jwtToken = jwtService.generateToken(savedUser);
        var refreshToken = jwtService.generateRefreshToken(savedUser);
        saveUserToken(savedUser, jwtToken);
        return new TokenResponse(jwtToken, refreshToken);

    }

    public TokenResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );
        var user = userRepository.findByEmail(request.email())
                .orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, jwtToken);
        return new TokenResponse(jwtToken, refreshToken);

    }

    private void revokeAllUserTokens(UserEntity user) {
        final List<TokenEntity> validUserTokens = tokenRepository
                .findAllValidTokensByUserId(user.getId());
        if (!validUserTokens.isEmpty()) {
            for (final TokenEntity token : validUserTokens) {
                token.setExpired(true);
                token.setRevoked(true);
            }
        }
        tokenRepository.saveAll(validUserTokens);

    }

    private void saveUserToken(UserEntity user, String jwtToken) {
        var token = TokenEntity.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenEntity.TokenType.BEARER)
                .revoked(false)
                .expired(false)
                .build();
        tokenRepository.save(token);
    }

    public TokenResponse refreshToken(final String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer")) {
            throw new IllegalArgumentException("Invalid token");

        }
        final String refreshToken = authHeader.substring(7);
        final String userEmail = jwtService.extractUsername(refreshToken);

        if (userEmail == null) {
            throw new IllegalArgumentException("Invalid Refresh token");

        }

        final UserEntity user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException(userEmail));

        if (!jwtService.isTokenValid(refreshToken, user)) {
            throw new IllegalArgumentException("Invalid Refresh token");
        }

        final String accessToken = jwtService.generateToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, accessToken);
        return new TokenResponse(accessToken, refreshToken);


    }
}
