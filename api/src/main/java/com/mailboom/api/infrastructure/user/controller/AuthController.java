package com.mailboom.api.infrastructure.user.controller;


import com.mailboom.api.application.user.usecase.command.CreateUserCommand;
import com.mailboom.api.infrastructure.user.dto.LoginRequest;
import com.mailboom.api.infrastructure.user.dto.NewUserRequest;
import com.mailboom.api.infrastructure.user.dto.TokenResponse;
import com.mailboom.api.infrastructure.security.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService service;

    @PostMapping("/register")
    public ResponseEntity<TokenResponse> register(@RequestBody final NewUserRequest request){
        final CreateUserCommand newUserCommand = new CreateUserCommand(
                request.email(),
                request.password(),
                request.name()
        );

        final TokenResponse token = service.register(newUserCommand);
        return ResponseEntity.ok(token);
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> authenticate(@RequestBody final LoginRequest request){
        final TokenResponse token = service.login(request);
        return ResponseEntity.ok(token);
    }

    @PostMapping("/refresh")
    public TokenResponse refreshToken(@RequestHeader (HttpHeaders.AUTHORIZATION) final String authHeader){
        return service.refreshToken(authHeader);
    }

}
