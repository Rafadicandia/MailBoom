package com.mailboom.api.infrastructure.controller;


import com.mailboom.api.infrastructure.dto.LoginRequest;
import com.mailboom.api.infrastructure.dto.NewUserRequest;
import com.mailboom.api.infrastructure.dto.TokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService service;

    @PostMapping("/register")
    public ResponseEntity<TokenResponse> register(@RequestBody final NewUserRequest request){
        final TokenResponse token = service.register(request);
        return ResponseEntity.ok(token);
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> authenticate(@RequestBody final LoginRequest request){
        final TokenResponse token = service.login(request);
        return ResponseEntity.ok(token);

    }

    @PostMapping("/refresh")
    public TokenResponse refreshToken(@RequestHeader (HttpHeaders.AUTHORIZATION) final String authHeader){
        return service.refreshToken(request);
    }

}
