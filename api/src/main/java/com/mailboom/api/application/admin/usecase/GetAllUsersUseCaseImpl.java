package com.mailboom.api.application.admin.usecase;

import com.mailboom.api.application.admin.in.port.GetAllUsersUseCase;
import com.mailboom.api.application.admin.usecase.command.GetAllUsersCommand;
import com.mailboom.api.domain.model.user.User;
import com.mailboom.api.domain.port.out.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class GetAllUsersUseCaseImpl implements GetAllUsersUseCase {
    private final UserRepository userRepository;

    @Override
    public List<User> execute(GetAllUsersCommand command) {

        return userRepository.findAllUsers();
    }
}
