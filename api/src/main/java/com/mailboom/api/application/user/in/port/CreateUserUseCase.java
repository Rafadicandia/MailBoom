package com.mailboom.api.application.user.in.port;

import com.mailboom.api.application.user.usecase.command.CreateUserCommand;
import com.mailboom.api.domain.model.User;

public interface CreateUserUseCase {
    User execute(CreateUserCommand command);
}
