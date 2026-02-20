package com.mailboom.api.infrastructure.user.controller;

import com.mailboom.api.application.user.in.port.DeleteUserUseCase;
import com.mailboom.api.application.user.in.port.GetUserUseCase;
import com.mailboom.api.application.user.in.port.UpdateUserUseCase;
import com.mailboom.api.application.user.usecase.command.DeleteUserCommand;
import com.mailboom.api.application.user.usecase.command.GetUserCommand;
import com.mailboom.api.application.user.usecase.command.UpdateUserCommand;
import com.mailboom.api.domain.model.user.User;
import com.mailboom.api.infrastructure.user.dto.UpdateUserRequest;
import com.mailboom.api.infrastructure.user.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/user")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
public class UserController {
    private final GetUserUseCase getUserUseCase;
    private final UpdateUserUseCase updateUserUseCase;
    private final DeleteUserUseCase deleteUserUseCase;

    @GetMapping("/{id}")
    @PreAuthorize("@userSecurity.isOwner(authentication, #id)")
    public ResponseEntity<UserResponse> getUser(@PathVariable String id) {
        User user = getUserUseCase.execute(new GetUserCommand(id));
        return ResponseEntity.ok(UserResponse.from(user));
    }

    @PutMapping("/{id}")
    @PreAuthorize("@userSecurity.isOwner(authentication, #id)")
    public ResponseEntity<UserResponse> updateUser(@PathVariable String id, @RequestBody UpdateUserRequest request) {
        User user = updateUserUseCase.execute(new UpdateUserCommand(id, request.name(), request.email(), request.password()));
        return ResponseEntity.ok(UserResponse.from(user));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@userSecurity.isOwner(authentication, #id)")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        deleteUserUseCase.execute(new DeleteUserCommand(id));
        return ResponseEntity.noContent().build();
    }
}
